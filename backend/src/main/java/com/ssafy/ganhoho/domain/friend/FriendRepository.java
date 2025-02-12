package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.entity.Friend;
import com.ssafy.ganhoho.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    // 보낸 대상 친구목록 조회, 즐겨찾기 오름차순
    @Query("SELECT f FROM Friend f " +
            "WHERE f.member = :member " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.ACCEPTED " +
            "ORDER BY f.isFavorite DESC")
    // 메서드의 member 변수를 :member 바인딩. <- findAcceptedFriendsByMember 호출시..
    List<Friend> findAcceptedFriendsByMember(@Param("member") Member member);

    // (로직 추가) 받은 대상 친구 목록 조회
    @Query("SELECT f FROM Friend f " +
            "WHERE f.friendLoginId = :loginId " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.ACCEPTED "+
            "ORDER BY f.isFavorite DESC")
    List<Friend> friendAcceptedByLoginId(@Param("loginId") String loginId);

    // 요청 보낸 대상 특정 회원의 대기 중인 친구 요청 목록 조회.
    @Query("SELECT f FROM Friend f " +
            "WHERE f.member = :member " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.PENDING " +
            "ORDER BY f.isFavorite DESC")
    List<Friend> findRequestsByMember(@Param("member") Member member);

    // 받은 대상 특정 회원의 대기중인 친구 요청 목록 조회
    @Query("SELECT f FROM Friend f " +
            "WHERE f.friendLoginId = :loginId " +
            "AND f.requestStatus = com.ssafy.ganhoho.domain.friend.constant.RequestStatus.PENDING " +
            "ORDER BY f.isFavorite DESC")
    List<Friend> findRequestsByLoginId(@Param("loginId") String loginId);

    // 특정 친구 관계 조회 (친구 삭제시 양방향 삭제)
    @Query("SELECT f FROM Friend f " +
            "WHERE f.member.loginId = :memberLoginId " +
            "AND f.friendLoginId = :friendLoginId")
    Optional<Friend> findByMemberLoginIdAndFriendLoginId(
            @Param("memberLoginId") String memberLoginId,
            @Param("friendLoginId") String friendLoginId
    );

    // 양방향 친구 관계 조회
    @Query("SELECT f FROM Friend f " +
            "WHERE (f.member.loginId = :memberLoginId AND f.friendLoginId = :friendLoginId) " +
            "OR (f.member.loginId = :friendLoginId AND f.friendLoginId = :memberLoginId)")
    Optional<Friend> findByMemberAndFriend(
            @Param("memberLoginId") String memberLoginId,
            @Param("friendLoginId") String friendLoginId
    );
}
