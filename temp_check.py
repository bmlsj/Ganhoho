from evdev import InputDevice, categorize, ecodes

device = InputDevice('/dev/input/event3')  # 위에서 확인한 리모컨 경로로 변경

print(f"🎯 {device.name} ({device.path}) 버튼 이벤트 감지 중...")

for event in device.read_loop():
    if event.type == ecodes.EV_KEY:
        key_event = categorize(event)
        print(f"🔘 버튼 입력: {key_event.keycode}, 상태: {key_event.keystate}")
