import os
import time

def main():
    try:
        # /dev/hidraw0 파일을 읽기 모드로 엽니다.
        with open('/dev/hidraw0', 'rb') as f:
            print("🔍 /dev/hidraw0 열림, 데이터 읽는 중...", flush=True)
            while True:
                data = f.read(64)
                if data:
                    print("Raw HID report:", data.hex(), flush=True)
                time.sleep(0.01)
    except Exception as e:
        print("오류 발생:", e)

if __name__ == "__main__":
    main()
