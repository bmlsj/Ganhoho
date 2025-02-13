# main.py
import asyncio
import queue
from bt_monitor import monitor_btmon
from device_save import monitor_registration
from alert_manager import process_alerts, resend_failed_alerts
from db_utils import init_db

async def main():
    print("[DEBUG] main() 시작", flush=True)
    init_db()  # 데이터베이스 초기화
    alert_queue = queue.Queue()

    # evdev 장치 경로 (환경에 맞게 수정하세요)
    # (등록용과 일반 이벤트 모니터링 모두 같은 장치를 사용한다면 동일하게 설정)
    device_path = "/dev/input/event3"
    print(f"[DEBUG] 사용 device_path: {device_path}", flush=True)
    # 각각의 비동기 태스크를 생성합니다.
    # 1. 일반 버튼 이벤트 처리 (등록된 기기에 대해서만)
    task_monitor = asyncio.create_task(monitor_btmon(alert_queue, device_path))
    # 2. 등록 모드: KEY_PLAYPAUSE 2초 이상 눌림 감지 시 등록 처리
    task_registration = asyncio.create_task(monitor_registration(device_path))
    # 3. 서버 전송 및 재전송 처리
    task_process_alerts = asyncio.create_task(process_alerts(alert_queue))
    task_resend = asyncio.create_task(resend_failed_alerts())

    await asyncio.gather(task_monitor, task_registration, task_process_alerts, task_resend)

if __name__ == "__main__":
    asyncio.run(main())
