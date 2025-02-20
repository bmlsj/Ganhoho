package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.entity.GroupSchedule;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Long> {
    // 특정 그룹 특정 월 스케줄 조회
    @Query("SELECT gs FROM GroupSchedule gs " +
            "WHERE gs.groupId = :groupId " +
            "AND gs.scheduleMonth = :yearMonth")
    Optional<GroupSchedule> findByGroupIdAndYearMonth(
            @Param("groupId") Long groupId,
            @Param("yearMonth") String yearMonth);

    // 특정 그룹 모든 스케줄 조회
    @Query("SELECT ws FROM WorkSchedule ws " +
            "WHERE ws.workScheduleDetailId IN " + // workSchedule
            "(SELECT gs.workScheduleDetailId FROM GroupSchedule gs WHERE gs.groupId = :groupId AND gs.scheduleMonth = :yearMonth)")
    List<WorkSchedule> findWorkScheduleByGroupIdAndYearMonth(
            @Param("groupId") Long groupId,
            @Param("yearMonth") String yearMonth);

    // 특정 그룹 모든 스케줄 조회(memberId로 직접연결)


}
