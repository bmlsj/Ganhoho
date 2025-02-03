package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupDto, Long> {
}
