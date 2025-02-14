# db_utils.py
import sqlite3
from config import DB_PATHS

def get_db_connection(db_type):
    """DB 연결 객체를 반환"""
    db_path = DB_PATHS.get(db_type)
    if not db_path:
        raise ValueError(f"Invalid DB type: {db_type}")
    return sqlite3.connect(db_path)

def init_db():
    """DB 초기화: 기기 정보와 전송 실패 알림 저장 테이블 생성"""
    # 기기 정보를 저장하는 devices.db (Bed 테이블)
    with get_db_connection("devices") as conn:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS Bed (
                mac_address TEXT PRIMARY KEY,
                bed_id TEXT NOT NULL,
                device_name TEXT NOT NULL
            );
        """)
        conn.commit()

    # 전송 실패 알림을 저장하는 alerts.db (FailedAlerts 테이블)
    # alert_type 컬럼 추가 (정수형, 기본값 0)
    with get_db_connection("alerts") as conn:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS FailedAlerts (
                alert_id INTEGER PRIMARY KEY AUTOINCREMENT,
                bed_id TEXT NOT NULL,
                content TEXT NOT NULL,
                received_time DATETIME NOT NULL,
                attempt_count INTEGER DEFAULT 0,
                alert_type INTEGER DEFAULT 0,
                sent_status BOOLEAN DEFAULT 0
            );
        """)
        conn.commit()
