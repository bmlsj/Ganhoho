package com.ssafy.ganhoho.domain.friend.dto;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 친구 즐겨찾기 수정
public class FriendFavoriteRequest {
    private Long friendMemberId;
    private Boolean isFavorite;
}
