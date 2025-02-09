package com.ssafy.ganhoho.domain.friend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 즐겨찾기 수정 요청")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 친구 즐겨찾기 수정
public class FriendFavoriteRequest {
    @Schema(description = "즐겨찾기 수정할 친구의 회원 ID", example = "1")
    private Long friendMemberId;

    @Schema(description = "즐겨찾기 여부 (true : 추가, false: 제거", example = "true")
    private Boolean isFavorite;
}
