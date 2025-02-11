package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.*;

import java.util.List;

public interface FriendService {
    // 로그인 사용자 친구목록 조회
    List<FriendListResponse> getFriendsList(Long memberId);
    // 친구 삭제
    FriendDeleteResponse deleteFriend(Long memberId, Long friendId);

    //친구요청 목록 조회
    List<FriendRequestListResponse> getFriendRequestList(Long memberId);

    // 친구 요청 승인/거절
    FriendRequestStatusResponse handleFriendRequest(Long memberId, Long friendId, FriendRequestStatusRequest request);

    //친구 추가요청
    FriendAddResponse addFriend(Long memberId, FriendAddRequest request);

    // 친구 즐겨찾기 수정
    FriendFavoriteResponse updateFriendFavorite(Long memberId, Long friendId, FriendFavoriteRequest request);
}
