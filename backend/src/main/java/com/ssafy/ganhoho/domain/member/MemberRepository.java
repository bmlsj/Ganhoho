package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import com.ssafy.ganhoho.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(Long memberId);

    List<Member> findMemberByLoginIdContainingIgnoreCase(String LoginId);

    List<Member> findMemberByNameContainingIgnoreCase(String name);

    List<Member> findMembersByNameContainingIgnoreCaseOrLoginIdContainingIgnoreCase(String name, String loginId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Member member WHERE member.memberId = :memberId")
    Optional<Integer> deleteMemberByMemberId(@Param("memberId") Long memberId);

    List<Member> findMembersByHospitalAndWard(String hospital, String ward);

}
