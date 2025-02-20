package com.ssafy.ganhoho.util

import com.ssafy.ganhoho.data.model.dto.schedule.FriendPublicSchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.WorkScheduleDto

// ✅ WorkScheduleDto를 MySchedule로 변환하여 캘린더에 표시
fun convertWorkScheduleToMySchedule(workSchedules: List<WorkScheduleDto>): List<MySchedule> {
    return workSchedules.map { work ->
        MySchedule(
            scheduleId = -1,  // 근무 일정은 임시 ID 사용
            startDt = work.workDate,
            endDt = work.workDate,  // 근무 일정은 당일 일정
            scheduleTitle = work.workType.toString(),
            scheduleColor = "#D1EEF2",  // 근무 일정 색 => 아직 결정 못함
            isPublic = true,
            isTimeSet = false
        )
    }
}


// ✅ WorkScheduleDto를 MySchedule로 변환하여 캘린더에 표시
fun convertFriendWorkScheduleToSchedule(workSchedules: List<WorkScheduleDto>): List<FriendPublicSchedule> {
    return workSchedules.map { work ->
        FriendPublicSchedule(
            scheduleId = -1,  // 근무 일정은 임시 ID 사용
            startDt = work.workDate,
            endDt = work.workDate,  // 근무 일정은 당일 일정
            scheduleTitle = work.workType.toString(),
            scheduleColor = "#D1EEF2",  // 근무 일정 색 => 아직 결정 못함
        )
    }
}
