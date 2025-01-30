package com.ssafy.ganhoho.domain.friend.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDeleteResponse {
    private Long friendId;
}
