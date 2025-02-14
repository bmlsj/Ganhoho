# device_save.py
import asyncio
import time
import subprocess
from evdev import InputDevice, categorize, ecodes
from db_utils import get_db_connection

DEBOUNCE_INTERVAL = 0.5  # 짧은 시간 내 반복 이벤트 무시
KEY_REGISTRATION = "KEY_PLAYPAUSE"
HOLD_THRESHOLD = 2.0     # 2초 이상 누르면 등록

def get_registered_device(identifier):
    """
    DB에서 device_name(여기서는 identifier)으로 등록된 기기를 조회.
    등록된 기기가 있으면 (mac_address, bed_id, device_name) 튜플을 반환.
    """
    print(f"[DEBUG] get_registered_device() called with identifier: {identifier}", flush=True)
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT mac_address, bed_id, device_name FROM Bed WHERE device_name = ?", (identifier,))
        result = cursor.fetchone()
        print(f"[DEBUG] get_registered_device() result: {result}", flush=True)
        return result

def generate_bed_id():
    """
    DB에 등록된 기기 수에 따라 새로운 bed_id를 생성합니다.
    예: 기존 기기 수가 2라면 새 기기는 "Bed_3"
    """
    print("[DEBUG] generate_bed_id() called", flush=True)
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM Bed")
        count = cursor.fetchone()[0]
        bed_id = f"Bed_{count + 1}"
        print(f"[DEBUG] generate_bed_id() generated: {bed_id}", flush=True)
        return bed_id

def save_device(mac_address, bed_id, device_name):
    """
    실제 MAC 주소, bed_id, device_name을 DB에 저장합니다.
    """
    print(f"[DEBUG] save_device() called with mac_address={mac_address}, bed_id={bed_id}, device_name={device_name}", flush=True)
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute(
            "INSERT OR IGNORE INTO Bed (mac_address, bed_id, device_name) VALUES (?, ?, ?)",
            (mac_address, bed_id, device_name)
        )
        conn.commit()
        print(f"✅ 장치 등록 완료: {device_name} -> {bed_id}, MAC: {mac_address}", flush=True)

def get_mac_address_from_bluetoothctl(device_name):
    """
    bluetoothctl devices 명령을 실행하여, device_name의 일부(예: 첫 번째 단어)가 포함된
    장치의 MAC 주소를 파싱합니다.
    """
    search_term = device_name.split()[0].lower()  # 예: "elecom"
    try:
        output = subprocess.check_output(["bluetoothctl", "devices"], universal_newlines=True)
        print(f"[DEBUG] bluetoothctl output:\n{output}", flush=True)
        for line in output.splitlines():
            if search_term in line.lower():
                parts = line.split()
                if len(parts) >= 2:
                    mac = parts[1]
                    print(f"[DEBUG] MAC address found: {mac}", flush=True)
                    return mac
        print(f"[DEBUG] '{device_name}' (검색어: {search_term})에 해당하는 MAC 주소를 찾지 못했습니다.", flush=True)
    except Exception as e:
        print(f"[ERROR] bluetoothctl 명령 실행 실패: {e}", flush=True)
    return None

async def monitor_registration(device_path):
    """
    evdev를 사용하여 KEY_PLAYPAUSE 이벤트를 모니터링합니다.
    KEY_PLAYPAUSE가 2초 이상 눌리면, 자동으로 bluetoothctl을 사용하여 MAC 주소를 획득하고,
    DB에 등록합니다.
    
    장치가 연결되지 않은 경우, 연결될 때까지 대기합니다.
    """
    print(f"[DEBUG] monitor_registration() called with device_path: {device_path}", flush=True)
    dev = None
    while True:
        try:
            dev = InputDevice(device_path)
            print(f"[DEBUG] Registration device 열림: {device_path}", flush=True)
            break
        except Exception as e:
            print(f"[ERROR] 장치 열기 실패: {e}. 등록 모드 대기 중...", flush=True)
            await asyncio.sleep(1)

    print(f"🔵 등록 모드 시작: {dev.name} at {dev.path}", flush=True)
    identifier = dev.name  # evdev의 장치 이름을 identifier로 사용
    print(f"[DEBUG] monitor_registration() identifier: {identifier}", flush=True)

    key_down_time = {}
    async for event in dev.async_read_loop():
        if event.type == ecodes.EV_KEY:
            key_event = categorize(event)
            key_code = key_event.keycode
            if isinstance(key_code, list):
                key_code = key_code[0]

            if key_code == KEY_REGISTRATION:
                if key_event.keystate == key_event.key_down:
                    if key_code not in key_down_time:
                        key_down_time[key_code] = time.time()
                        print(f"[DEBUG] {KEY_REGISTRATION} 시작 시간: {key_down_time[key_code]:.3f}", flush=True)
                elif key_event.keystate == key_event.key_up:
                    if key_code in key_down_time:
                        duration = time.time() - key_down_time[key_code]
                        print(f"[DEBUG] {KEY_REGISTRATION} 지속 시간: {duration:.3f}초", flush=True)
                        if duration >= HOLD_THRESHOLD:
                            print(f"[INFO] 등록 조건 충족: {KEY_REGISTRATION} {duration:.3f}초 눌림", flush=True)
                            mac_address = get_mac_address_from_bluetoothctl(identifier)
                            if mac_address:
                                bed_id = generate_bed_id()
                                save_device(mac_address, bed_id, identifier)
                            else:
                                print("[ERROR] MAC 주소 자동 획득 실패", flush=True)
                        else:
                            print(f"[INFO] {KEY_REGISTRATION}이 {HOLD_THRESHOLD}초 미만 눌렸으므로 등록하지 않습니다.", flush=True)
                        key_down_time.pop(key_code)
