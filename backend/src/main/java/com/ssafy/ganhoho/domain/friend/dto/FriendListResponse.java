package com.ssafy.ganhoho.domain.friend.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendListResponse {
    private Long friendId;
    private String friendLoginId;
    private String name;
    private String hospital;
    private String ward;
    private Boolean isFavorite;
}
