package com.ssafy.ganhoho.domain.friend.constant;

public enum RequestStatus {
    PENDING("대기 중"),
    ACCEPTED("수락함");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}