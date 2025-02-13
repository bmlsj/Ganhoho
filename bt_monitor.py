# bt_monitor.py
import os
import re
import time
import asyncio
from evdev import InputDevice, categorize, ecodes
from db_utils import get_db_connection
from config import BUTTON_MAPPING  # evdev용 매핑 테이블

# DB 관련 함수: 이제 device_name 컬럼으로 조회하도록 수정
def get_bed_name(identifier):
    """
    DB에서 identifier(여기서는 device name)를 사용해 기기 이름(bed_id)을 조회합니다.
    """
    print(f"[DEBUG] get_bed_name() called with identifier: {identifier}", flush=True)
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT bed_id FROM Bed WHERE device_name = ?", (identifier,))
        result = cursor.fetchone()
        print(f"[DEBUG] get_bed_name() result: {result}", flush=True)
        return result[0] if result else None

# 디바운스 및 상태 관리 변수
button_state = {}      # key: evdev keycode, value: Boolean (이벤트 처리 여부)
last_event_time = {}   # key: evdev keycode, value: 마지막 이벤트 시간 (초)
DEBOUNCE_INTERVAL = 0.5  # 초 단위: 동일 이벤트 반복 무시 간격
RELEASE_TIMEOUT = 1.0    # 버튼 해제 타임아웃 (예: 1초 후 자동 초기화)


async def monitor_btmon(alert_queue, device_path):
    """
    evdev를 사용하여 이벤트 장치에서 버튼 이벤트를 읽어오고,
    BUTTON_MAPPING에 따라 매핑된 이벤트를 처리한 후,
    DB에서 등록된 기기(여기서는 device_name으로 등록된)를 기준으로 alert_queue에 전달합니다.
    
    매 루프마다 DB를 조회하여 등록 여부를 확인합니다.
    """
    try:
        dev = InputDevice(device_path)
    except Exception as e:
        print(f"[ERROR] 장치 열기 실패: {e}", flush=True)
        return

    print(f"🔵 EVDEV 모니터링 시작: {dev.name} at {dev.path}", flush=True)
    identifier = dev.name  # evdev에서는 MAC 주소 대신 device name 사용

    async for event in dev.async_read_loop():
        # 매 루프마다 DB 등록 여부 재확인
        bed_name = get_bed_name(identifier)
        if not bed_name:
            print(f"[DEBUG] '{identifier}' 장치가 아직 DB에 등록되어 있지 않습니다. 이벤트 처리 대기...", flush=True)
            await asyncio.sleep(1)  # 1초 대기 후 다시 확인
            continue
        else:
            print(f"[DEBUG] 기기 등록 확인: {bed_name}", flush=True)
        
        now = time.time()
        # RELEASE_TIMEOUT: 오래된 버튼 상태 초기화
        for key in list(button_state.keys()):
            elapsed = now - last_event_time.get(key, 0)
            if elapsed > RELEASE_TIMEOUT:
                print(f"[DEBUG] {key} 상태 초기화 (elapsed {elapsed:.3f}s > {RELEASE_TIMEOUT}s)", flush=True)
                button_state[key] = False

        print(f"[DEBUG] button_state: {button_state}", flush=True)
        print(f"[DEBUG] last_event_time: {last_event_time}", flush=True)

        if event.type == ecodes.EV_KEY:
            key_event = categorize(event)
            if key_event.keystate == key_event.key_down:
                key_code = key_event.keycode
                if isinstance(key_code, list):
                    key_code = key_code[0]
                print(f"[DEBUG] Detected key down event: {key_code}", flush=True)
                now = time.time()
                if key_code in last_event_time and (now - last_event_time[key_code]) < DEBOUNCE_INTERVAL:
                    print(f"[DEBUG] Debounced {key_code}: {now - last_event_time[key_code]:.3f}s elapsed", flush=True)
                    continue
                last_event_time[key_code] = now

                if key_code in BUTTON_MAPPING:
                    response_text = BUTTON_MAPPING[key_code]
                    print(f"[DEBUG] 매핑된 버튼: {key_code} -> {response_text}", flush=True)
                    print(f"🎯 버튼 감지: {bed_name} - {response_text}", flush=True)
                    alert_queue.put((bed_name, response_text))
                    button_state[key_code] = True

            elif key_event.keystate == key_event.key_up:
                key_code = key_event.keycode
                if isinstance(key_code, list):
                    key_code = key_code[0]
                print(f"[DEBUG] Key released: {key_code}", flush=True)
                button_state[key_code] = False
