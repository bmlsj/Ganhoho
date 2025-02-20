# alert_manager.py
import aiohttp
import asyncio
from datetime import datetime
from db_utils import get_db_connection
from config import SERVER_URL, QUEUE_CHECK_INTERVAL, RESEND_INTERVAL, MAX_RESEND_ATTEMPTS, JWT_TOKEN, HOSPITAL_NAME, WARD_NAME

async def send_to_server(session, bed_id, response_text, type_value):
    """서버에 알림 전송 (API 명세에 맞게)"""
    url = SERVER_URL  # 예: "http://server-endpoint/api/notifications/button-patterns"
    print("URL 주소!!!!!!!!!!!!!!!!!:",url)
    message = f"{bed_id}에서 {response_text}호출"
    title = response_text
    data = {
        "message": message,
        "title": title,
        "type": type_value,
        "hospital": HOSPITAL_NAME,
        "ward": WARD_NAME
    }
    headers = {
        "Authorization": f"{JWT_TOKEN}"
    }
    try:
        async with session.post(url, json=data, headers=headers, timeout=5) as response:
            print(f"[DEBUG] HTTP POST 응답 상태: {response.status}", flush=True)
            return response.status == 200
    except aiohttp.ClientError as e:
        print(f"[DEBUG] ClientError in send_to_server: {e}", flush=True)
        return False

def save_failed_alert(bed_id, content, type_value):
    """전송 실패 알림을 DB에 저장 (alert_type 추가)"""
    with get_db_connection("alerts") as conn:
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO FailedAlerts (bed_id, content, received_time, alert_type)
            VALUES (?, ?, ?, ?);
        """, (bed_id, content, datetime.now().isoformat(), type_value))
        conn.commit()

async def process_alerts(alert_queue):
    """큐에서 알림을 읽어 서버 전송 및 실패 시 DB 저장 처리"""
    async with aiohttp.ClientSession() as session:
        while True:
            if not alert_queue.empty():
                # alert_queue 아이템: (bed_id, response_text, type_value)
                item = alert_queue.get()
                if len(item) == 3:
                    bed_id, response_text, type_value = item
                else:
                    bed_id, response_text = item
                    type_value = 0
                print(f"[Alert Manager] 처리 중인 알림: {bed_id} - {response_text} (type: {type_value})", flush=True)
                success = await send_to_server(session, bed_id, response_text, type_value)
                if success:
                    print(f"[Alert Manager] 서버 전송 성공: {bed_id} - {response_text}", flush=True)
                else:
                    print(f"[Alert Manager] 서버 전송 실패: {bed_id} - {response_text}. 실패 알림 DB에 저장합니다.", flush=True)
                    save_failed_alert(bed_id, f"{response_text}", type_value)
            await asyncio.sleep(QUEUE_CHECK_INTERVAL)

async def resend_failed_alerts():
    """DB에 저장된 전송 실패 알림을 주기적으로 재전송하며, 최대 재전송 횟수를 관리함.
    재전송 우선순위는 received_time이 빠른 순으로 진행합니다.
    """
    async with aiohttp.ClientSession() as session:
        while True:
            with get_db_connection("alerts") as conn:
                cursor = conn.cursor()
                # sent_status가 0인 항목을 received_time ASC 순으로 조회
                cursor.execute("""
                    SELECT alert_id, bed_id, content, attempt_count, alert_type 
                    FROM FailedAlerts 
                    WHERE sent_status = 0 
                    ORDER BY received_time ASC
                """)
                failed_alerts = cursor.fetchall()
                for alert_id, bed_id, content, attempt_count, alert_type in failed_alerts:
                    if attempt_count >= MAX_RESEND_ATTEMPTS:
                        print(f"[Resend] 최대 재전송 횟수 도달: {bed_id}: {content} (시도: {attempt_count}) - 재전송 중단", flush=True)
                        cursor.execute("UPDATE FailedAlerts SET sent_status = 1 WHERE alert_id = ?", (alert_id,))
                        conn.commit()
                        continue
                    success = await send_to_server(session, bed_id, content, alert_type)
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
