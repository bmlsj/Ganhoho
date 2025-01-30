package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberDto, Long> {
    Optional<MemberDto> findByMemberId(Long memberId);
}
