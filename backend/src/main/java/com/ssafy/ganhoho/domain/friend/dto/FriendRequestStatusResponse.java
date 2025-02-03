package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestStatusResponse {
    // 친구 요청 승인 및 거절 Response
    private Long friendId;
    private RequestStatus requestStatus;
}
