package com.ssafy.ganhoho.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 즐겨찾기 수정 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendFavoriteResponse {
    @Schema(description = "수정 성공 여부", example = "true")
    @Builder.Default
    private boolean success = true;
}