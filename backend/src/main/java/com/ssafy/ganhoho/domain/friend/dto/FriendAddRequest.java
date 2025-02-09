package com.ssafy.ganhoho.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "친구 추가 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendAddRequest {
    @Schema(description = "친구 요청을 보낼 대상의 로그인 ID", example = "ssafy1234")
    private String friendLoginId;
}
