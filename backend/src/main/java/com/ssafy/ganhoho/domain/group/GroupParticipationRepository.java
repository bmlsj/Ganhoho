package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.entity.GroupParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupParticipationRepository extends JpaRepository<GroupParticipation, Long> {
    boolean existsByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    //그룹 id 찾기
    List<GroupParticipation> findByGroupId(Long groupId);

    // 그룹 탈퇴
    int deleteByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    List<GroupParticipation> findByMemberId(Long memberId);
}
