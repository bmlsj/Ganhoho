package com.ssafy.ganhoho.domain.friend.constant;

public enum RequestStatus {
    대기_중("대기 중"),
    수락함("수락함");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}