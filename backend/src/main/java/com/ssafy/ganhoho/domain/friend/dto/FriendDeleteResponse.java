package com.ssafy.ganhoho.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 삭제 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDeleteResponse {
    @Schema(description = "삭제된 친구 관계의 ID", example = "3")
    private Long friendId;
}
