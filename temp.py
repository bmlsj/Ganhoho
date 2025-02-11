#scp temp.py D209@192.168.103.30:/home/D209/

from evdev import list_devices, InputDevice

# 현재 연결된 입력 장치 확인
devices = [InputDevice(path) for path in list_devices()]
if not devices:
    print("❌ 연결된 입력 장치가 없습니다.")
else:
    for device in devices:
        print(f"✅ 장치 감지: {device.path} - {device.name}")
