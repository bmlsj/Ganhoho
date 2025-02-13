# alert_manager.py
import aiohttp
import asyncio
from datetime import datetime
from db_utils import get_db_connection
from config import SERVER_URL, QUEUE_CHECK_INTERVAL, RESEND_INTERVAL, MAX_RESEND_ATTEMPTS

async def send_to_server(session, content):
    """서버에 알림 전송"""
    url = SERVER_URL
    data = {
        "content": content,
        "timestamp": datetime.now().isoformat()
    }
    try:
        async with session.post(url, json=data, timeout=5) as response:
            return response.status == 200
    except aiohttp.ClientError:
        return False

def save_failed_alert(bed_id, content):
    """전송 실패 알림을 DB에 저장"""
    with get_db_connection("alerts") as conn:
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO FailedAlerts (bed_id, content, received_time)
            VALUES (?, ?, ?);
        """, (bed_id, content, datetime.now().isoformat()))
        conn.commit()

async def process_alerts(alert_queue):
    """큐에서 알림을 읽어 서버 전송 및 실패 시 DB 저장 처리"""
    async with aiohttp.ClientSession() as session:
        while True:
            if not alert_queue.empty():
                bed_name, response_text = alert_queue.get()
                content = f"{bed_name}: {response_text}"
                print(f"[Alert Manager] 처리 중인 알림: {content}", flush=True)
                success = await send_to_server(session, content)
                if success:
                    print(f"[Alert Manager] 서버 전송 성공: {content}", flush=True)
                else:
                    print(f"[Alert Manager] 서버 전송 실패: {content}. 실패 알림 DB에 저장합니다.", flush=True)
                    save_failed_alert(bed_name, content)
            await asyncio.sleep(QUEUE_CHECK_INTERVAL)

async def resend_failed_alerts():
    """
    DB에 저장된 전송 실패 알림을 주기적으로 재전송합니다.
    - 재전송 성공한 알림은 DB에서 삭제합니다.
    - 재전송 실패 시, attempt_count를 증가시킵니다.
    - 최대 재전송 횟수에 도달한 알림은 재전송 시도를 중단(아무 작업도 하지 않음)하며 DB에 그대로 남겨둡니다.
    """
    async with aiohttp.ClientSession() as session:
        while True:
            with get_db_connection("alerts") as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT alert_id, bed_id, content, attempt_count FROM FailedAlerts WHERE sent_status = 0")
                failed_alerts = cursor.fetchall()
                for alert_id, bed_id, content, attempt_count in failed_alerts:
                    # 최대 재전송 횟수에 도달한 경우 재전송 시도 중단 (아무 작업도 하지 않음)
                    if attempt_count >= MAX_RESEND_ATTEMPTS:
                        print(f"[Resend] 최대 재전송 횟수 도달: {content} (시도: {attempt_count}) - 재전송 중단", flush=True)
                        continue
                    success = await send_to_server(session, content)
                    if success:
                        cursor.execute("DELETE FROM FailedAlerts WHERE alert_id = ?", (alert_id,))
                        conn.commit()
                        print(f"[Resend] 서버 전송 성공으로 삭제됨: {content}", flush=True)
                    else:
                        cursor.execute("""
                            UPDATE FailedAlerts 
                            SET attempt_count = attempt_count + 1 
                            WHERE alert_id = ?;
                        """, (alert_id,))
                        conn.commit()
                        print(f"[Resend] 전송 실패, 재시도 횟수 증가: {content} (시도: {attempt_count+1})", flush=True)
            await asyncio.sleep(RESEND_INTERVAL)
