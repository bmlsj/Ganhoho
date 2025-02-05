package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupScheduleDto;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupScheduleRepository extends JpaRepository<GroupScheduleDto, Long> {
    // 특정 그룹 특정 월 스케줄 조회
    @Query("SELECT gs FROM GroupScheduleDto gs " +
            "WHERE gs.groupId = :groupId " +
            "AND gs.scheduleMonth = :yearMonth")
    Optional<GroupScheduleDto> findByGroupIdAndYearMonth(
            @Param("groupId") Long groupId,
            @Param("yearMonth") String yearMonth);

    // 특정 그룹 모든 스케줄 조회
    @Query("SELECT ws FROM WorkSchedule ws " +
            "WHERE ws.groupScheduleDetailId IN " + // 추후 work_schedule_detail_id 로 수정해야함
            "(SELECT gs.workScheduleDetailId FROM GroupScheduleDto gs WHERE gs.groupId = :groupId AND gs.scheduleMonth = :yearMonth)")
    List<WorkSchedule> findWorkScheduleByGroupIdAndYearMonth(
            @Param("groupId") Long groupId,
            @Param("yearMonth") String yearMonth);

}
