package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.FriendDeleteResponse;
import com.ssafy.ganhoho.domain.friend.dto.FriendListResponse;
import com.ssafy.ganhoho.domain.friend.dto.FriendRequestListResponse;

import java.util.List;

public interface FriendService {
    // 로그인 사용자 친구목록 조회
    List<FriendListResponse> getFriendsList(Long memberId);
    // 친구 삭제
    FriendDeleteResponse deleteFriend(Long memberId, Long friendId);

    //친구요청 목록 조회
    List<FriendRequestListResponse> getFriendRequestList(Long memberId);
}
