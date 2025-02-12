package com.ssafy.ganhoho.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 추가 요청 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendAddResponse {
    @Schema(description = "단순 요청 처리 여부", example = "true")
    @Builder.Default
    private boolean success = true;
}
