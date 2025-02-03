package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.PersonalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule, Long> {
    List<PersonalSchedule> findByMemberId(Long memberId);
} 