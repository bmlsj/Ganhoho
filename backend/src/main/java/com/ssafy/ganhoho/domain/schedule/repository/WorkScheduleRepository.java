package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByMemberId(Long memberId);
    List<WorkSchedule> findByMemberIdAndWorkDateBetween(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate);
    //s
}