package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 요청 처리 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestStatusResponse {
    @Schema(description = "처리된 친구 요청 Id", example = "3")
    private Long friendId;

    @Schema(description = "처리된 요청 상태 (ACCEPTED, PENDING)" , example = "ACCEPTED")
    private RequestStatus requestStatus;
}
