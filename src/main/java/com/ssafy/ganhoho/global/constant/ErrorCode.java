package com.ssafy.ganhoho.global.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_ACCESS_TOKEN("액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    EXIST_ID("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_MEMBER("등록되지 않는 회원입니다.", HttpStatus.UNAUTHORIZED),
    EXIST_REQUEST("존재하는 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_DATA("데이터가 존재하지 않습니다.",HttpStatus.OK),
    INVALID_FCM_TOKEN("유효하지 않은 fcm 토큰입니다", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
