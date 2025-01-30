package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.FriendDto;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<FriendDto, Long> {
    // 특정 회원의 수락된 친구목록 조회, 즐겨찾기 오름차순
    @Query("SELECT f FROM FriendDto f " +
            "WHERE f.member = :member " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.ACCEPTED " +
            "ORDER BY f.isFavorite DESC")
    // 메서드의 member 변수를 :member 바인딩. <- findAcceptedFriendsByMember 호출시..
    List<FriendDto> findAcceptedFriendsByMember(@Param("member") MemberDto member);

    // 특정 회원의 대기 중인 친구 요청 목록 조회
    @Query("SELECT f FROM FriendDto f " +
            "WHERE f.member = :member " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.PENDING " +
            "ORDER BY f.isFavorite DESC")
    List<FriendDto> findRequestsByMember(@Param("member") MemberDto member);
}
