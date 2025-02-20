package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "친구 요청 처리 요청")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestStatusRequest {

    @Schema(description = "요청 처리 상태 (ACCEPTED, PENDING", example = "ACCEPTED")
    private RequestStatus requestStatus;
}
