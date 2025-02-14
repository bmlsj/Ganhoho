# config.py

DB_PATHS = {
    "alerts": "alerts.db",   # 알림 정보를 저장하는 DB
    "devices": "devices.db"   # 기기 정보를 저장하는 DB
}

SERVER_URL = "http://i12d209.p.ssafy.io/api/notifications/button-pattens"  # 서버 URL

JWT_TOKEN = "액세스 키나 공개 키를 사용해보자! "

# evdev 리모컨 버튼 데이터 매핑: (메시지, type)
BUTTON_MAPPING = {
    "KEY_NEXTSONG": ("기본", 2),       # 상 (Up)
    "KEY_PREVIOUSSONG": ("긴급", 1),    # 좌 (Left)
    "KEY_VOLUMEUP": ("의료", 3),         # 우 (Right)
    "KEY_VOLUMEDOWN": ("기타", 4)        # 하 (Down)
}


QUEUE_CHECK_INTERVAL = 0.01  # 큐 확인 주기 (초)
RESEND_INTERVAL = 10         # 실패 알림 재전송 주기 (초)
MAX_RESEND_ATTEMPTS = 5