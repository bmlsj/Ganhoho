package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleDetailRepository extends JpaRepository<ScheduleDetail, Long> {
    List<ScheduleDetail> findByPersonalSchedule_ScheduleId(Long scheduleId);
} 