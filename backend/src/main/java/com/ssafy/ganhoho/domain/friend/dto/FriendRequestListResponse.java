package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestListResponse {
    @Schema(description = "friendId와 같음", example = "3")
    private Long friendRequestId;

    @Schema(description = "친구 요청을 보낸 사용자의 로그인 ID", example = "minseok1234")
    private String friendLoginId; // 요청 보낸사람 Id

    @Schema(description = "친구 요청을 보낸 사용자의 이름", example = "John Doe")
    private String name;

    @Schema(description = "요청 보낸 사용자의 소속 병원", example = "seoul Medical Center")
    private String hospital;

    @Schema(description = "요청 보낸 사용자의 소속 병동", example = "b20")
    private String ward; //소속된 사람 병동 정보

    @Schema(description = "친구 요청 상태", example = "PENDING")
    private RequestStatus requestStatus;
}
