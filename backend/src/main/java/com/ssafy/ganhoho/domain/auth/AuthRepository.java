package com.ssafy.ganhoho.domain.auth;

import com.ssafy.ganhoho.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);

    Boolean existsByLoginId(String loginId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member member SET member.appFcmToken = :appFcmToken WHERE member.memberId = :memberId")
    Optional<Integer> updateFcmToken(@Param("appFcmToken") String appFcmToken, @Param("memberId") Long memberId);
}
