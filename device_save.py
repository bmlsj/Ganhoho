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
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT mac_address, bed_id, device_name FROM Bed WHERE device_name = ?", (identifier,))
        return cursor.fetchone()

def generate_bed_id():
    """
    DB에 등록된 기기 수에 따라 새로운 bed_id를 생성합니다.
    예: 기존 기기 수가 2라면 새 기기는 "Bed_3"
    """
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM Bed")
        count = cursor.fetchone()[0]
        return f"Bed_{count + 1}"

def save_device(mac_address, bed_id, device_name):
    """
    실제 MAC 주소, bed_id, device_name을 DB에 저장합니다.
    """
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
    bluetoothctl devices 명령을 실행하여, device_name과 일치하는 장치의 MAC 주소를 파싱합니다.
    실제 환경에서는 device_name이 고유하지 않을 수 있으므로 추가 필터링이 필요할 수 있습니다.
    예시로, bluetoothctl의 출력에서 "Device XX:XX:XX:XX:XX:XX" 형식으로 MAC 주소를 추출합니다.
    """
    try:
        output = subprocess.check_output(["bluetoothctl", "devices"], universal_newlines=True)
        # 출력 예시: "Device CF:6A:D1:F4:47:AB Beauty-R1"
        for line in output.splitlines():
            if device_name in line:
                parts = line.split()
                if len(parts) >= 2:
                    mac = parts[1]
                    return mac
        print(f"[DEBUG] '{device_name}'에 해당하는 MAC 주소를 찾지 못했습니다.", flush=True)
    except Exception as e:
        print(f"[ERROR] bluetoothctl 명령 실행 실패: {e}", flush=True)
    return None

async def monitor_registration(device_path):
    """
    evdev를 사용하여 KEY_PLAYPAUSE 이벤트를 모니터링합니다.
    KEY_PLAYPAUSE가 2초 이상 눌리면, 자동으로 bluetoothctl을 사용하여 MAC 주소를 획득하고,
    DB에 등록합니다.
    """
    try:
        dev = InputDevice(device_path)
    except Exception as e:
        print(f"[ERROR] 장치 열기 실패: {e}", flush=True)
        return

    print(f"🔵 등록 모드 시작: {dev.name} at {dev.path}", flush=True)
    identifier = dev.name  # evdev의 장치 이름을 identifier로 사용

    key_down_time = {}
    async for event in dev.async_read_loop():
        if event.type == ecodes.EV_KEY:
            key_event = categorize(event)
            key_code = key_event.keycode
            if isinstance(key_code, list):
                key_code = key_code[0]

            if key_code == KEY_REGISTRATION:
                if key_event.keystate == key_event.key_down:
                    # 최초 key_down 시각 저장
                    if key_code not in key_down_time:
                        key_down_time[key_code] = time.time()
                        print(f"[DEBUG] {KEY_REGISTRATION} 시작 시간: {key_down_time[key_code]:.3f}", flush=True)
                elif key_event.keystate == key_event.key_up:
                    if key_code in key_down_time:
                        duration = time.time() - key_down_time[key_code]
                        print(f"[DEBUG] {KEY_REGISTRATION} 지속 시간: {duration:.3f}초", flush=True)
                        # 2초 이상 눌렸으면 등록 진행
                        if duration >= HOLD_THRESHOLD:
                            print(f"[INFO] 등록 조건 충족: {KEY_REGISTRATION} {duration:.3f}초 눌림", flush=True)
                            # 자동으로 MAC 주소 획득
                            mac_address = get_mac_address_from_bluetoothctl(identifier)
                            if mac_address:
                                bed_id = generate_bed_id()
                                save_device(mac_address, bed_id, identifier)
                            else:
                                print("[ERROR] MAC 주소 자동 획득 실패", flush=True)
                        else:
                            print(f"[INFO] {KEY_REGISTRATION}이 {HOLD_THRESHOLD}초 미만 눌렸으므로 등록하지 않습니다.", flush=True)
                        key_down_time.pop(key_code)

if __name__ == "__main__":
    # 실제 등록 모드로 사용할 evdev 장치 경로 (예: /dev/input/event4)
    device_path = "/dev/input/event4"  # 환경에 맞게 수정하세요.
    asyncio.run(monitor_registration(device_path))
