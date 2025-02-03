package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestStatusRequest {
    // 친구 요청 승인 및 거절 Request
    private RequestStatus requestStatus;
}
