# main.py
import asyncio
import queue
from bt_monitor import monitor_btmon
from alert_manager import process_alerts, resend_failed_alerts
from db_utils import init_db


async def main():
    """메인 실행 함수: DB 초기화, 알림 큐 생성, bt_monitor와 알림 처리 작업을 비동기로 실행"""
    init_db()  # 데이터베이스 초기화
    alert_queue = queue.Queue()

    # bt_monitor는 블로킹 함수이므로, asyncio.to_thread를 사용해 별도 스레드에서 실행
    bt_monitor_task = asyncio.to_thread(monitor_btmon, alert_queue)

    await asyncio.gather(
        bt_monitor_task,
        process_alerts(alert_queue),
        resend_failed_alerts()
    )


if __name__ == "__main__":
    asyncio.run(main())
