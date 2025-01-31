package com.ssafy.ganhoho.domain.friend.dto;

import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequestListResponse {
    private Long friendRequestId;
    private String friendLoginId; // 요청 보낸사람 Id
    private String name;
    private String hospital;
    private String ward; //소속된 사람 병동 정보
    private RequestStatus requestStatus;
}
