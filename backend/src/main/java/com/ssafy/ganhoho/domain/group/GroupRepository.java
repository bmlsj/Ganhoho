package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupDto, Long> {
    // memberId를 기준으로 사용자가 참여하고 있는 그룹들 조회
    @Query("SELECT g FROM GroupDto g " +
            "JOIN GroupParticipationDto gp ON g.groupId = gp.groupId " +
            "WHERE gp.memberId = :memberId")
    List<GroupDto> findGroupByMemberId(@Param("memberId") Long memberId);
}
