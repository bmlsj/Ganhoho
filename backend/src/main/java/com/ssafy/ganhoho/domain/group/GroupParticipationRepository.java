package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupParticipationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupParticipationRepository extends JpaRepository<GroupParticipationDto, Long> {
    boolean existsByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);
}
