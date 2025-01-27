package com.ssafy.ganhoho.global.error;

import com.ssafy.ganhoho.global.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse(final int status, final String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(final int status, final String message) {
        return new ErrorResponse(status, message);
    }
}
