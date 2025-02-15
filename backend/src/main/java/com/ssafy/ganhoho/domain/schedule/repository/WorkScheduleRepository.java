package com.ssafy.ganhoho.domain.schedule.repository;

import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByMemberId(Long memberId);
    List<WorkSchedule> findByMemberIdAndWorkDateBetween(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    //s
    Optional<WorkSchedule> findByMemberIdAndWorkDate(Long memberId, LocalDateTime workDate);

    // 특정 회원의 특정 월 근무스케줄 조회
    @Query("SELECT ws FROM WorkSchedule ws " +
            "WHERE ws.memberId = :memberId " +
            "AND FUNCTION('DATE_FORMAT', ws.workDate, '%Y-%m') = :yearMonth")
    List<WorkSchedule> findByMemberIdAndMonthYear(
            @Param("memberId") Long memberId,
            @Param("yearMonth") String yearMonth);
}