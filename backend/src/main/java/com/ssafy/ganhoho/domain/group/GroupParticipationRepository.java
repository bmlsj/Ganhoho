package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupParticipationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupParticipationRepository extends JpaRepository<GroupParticipationDto, Long> {
    boolean existsByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    //그룹 id 찾기
    List<GroupParticipationDto> findByGroupId(Long groupId);
}
