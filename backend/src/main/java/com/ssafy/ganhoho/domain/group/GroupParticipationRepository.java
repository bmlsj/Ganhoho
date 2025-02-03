package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupParticipationDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupParticipationRepository extends JpaRepository<GroupParticipationDto, Long> {
}
