package com.ssafy.ganhoho.global.constant;

public enum FcmConstant {
    FIREBASE_POST_MESSAGE_URL("https://fcm.googleapis.com/v1/projects/ssafy-common-d209/messages:send"),
    FIREBASE_DEVICE_GROUP_URL("https://fcm.googleapis.com/fcm/notification"),
    FIREBASE_KEY_FILE("firebase_service_key.json"),
    FIREBASE_PROJECT_ID("ssafy-common-d209"),
    FIREBASE_SENDER_ID("447664676410");

    private final String value;

    FcmConstant(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
