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
    
    # 실제 리모컨 이벤트 장치 경로 (환경에 맞게 수정하세요)
    device_path = "/dev/input/event3"
    print(f"[DEBUG] 사용 device_path: {device_path}", flush=True)
    
    task_monitor = asyncio.create_task(monitor_btmon(alert_queue, device_path))
    task_registration = asyncio.create_task(monitor_registration(device_path))
    task_process_alerts = asyncio.create_task(process_alerts(alert_queue))
    task_resend = asyncio.create_task(resend_failed_alerts())

    await asyncio.gather(task_monitor, task_registration, task_process_alerts, task_resend)

if __name__ == "__main__":
    asyncio.run(main())
