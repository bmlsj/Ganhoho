# config.py

DB_PATHS = {
    "alerts": "alerts.db",   # 알림 정보를 저장하는 DB
    "devices": "devices.db"   # 기기 정보를 저장하는 DB
}

SERVER_URL = "http://server-endpoint/alerts"  # 서버 URL

# 블루투스 리모컨 버튼 데이터 매핑
BUTTON_MAPPING = {
    "011880": "기본",      # 상 (Up)
    "01f87f": "의료",  # 하 (Down)
    "288011": "긴급",  # 좌 (Left)
    "ff1780": "기타"   # 우 (Right)
}

QUEUE_CHECK_INTERVAL = 0.01  # 큐 확인 주기 (초)
RESEND_INTERVAL = 10         # 실패 알림 재전송 주기 (초)
MAX_RESEND_ATTEMPTS = 5