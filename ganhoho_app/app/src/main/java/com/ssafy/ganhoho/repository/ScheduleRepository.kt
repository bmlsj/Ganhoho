package com.ssafy.ganhoho.repository

import com.ssafy.ganhoho.data.model.dto.group.WorkScheduleDto
import com.ssafy.ganhoho.data.model.dto.schedule.FriendSchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.response.schedule.ScheduleUpdateResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class ScheduleRepository {

    // 내 근무 스케쥴 조회
    suspend fun getMyWorkSchedule(
        token: String,
        yearMonth: String
    ): Result<List<WorkScheduleDto>> {
        return try {
            val response =
                RetrofitUtil.scheduleService.getMyWorkSchedule("Bearer $token", yearMonth)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 내 근무 스케쥴 수정
    suspend fun updateMyWorkSchedule(
        token: String,
        workScheduleId: Long,
        request: WorkScheduleDto
    ): Result<ScheduleUpdateResponse> {
        return try {
            val response = RetrofitUtil.scheduleService.updateMyWorkSchedule(
                "Bearer $token",
                workScheduleId, request
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 개인 스케쥴 조회
    suspend fun getMySchedule(
        token: String
    ): Result<List<MySchedule>> {
        return try {
            val response = RetrofitUtil.scheduleService.getMySchedule(
                "Bearer $token"
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 개인 스케쥴 수정
    suspend fun updateSchedule(
        token: String,
        scheduleId: Long,
        request: MySchedule
    ): Result<Void> {
        return try {
            val response = RetrofitUtil.scheduleService.updateSchedule(
                "Bearer $token", scheduleId, request
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 개인 스케쥴 추가
    suspend fun addMySchedule(
        token: String,
        request: MyScheduleRequest
    ): Result<Void> {
        return try {
            val response = RetrofitUtil.scheduleService.addMySchedule(
                "Bearer $token", request
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 친구 근무 스케쥴 조회
    suspend fun getFriendSchedule(
        token: String,
        memberId: Long
    ): Result<List<WorkScheduleDto>> {
        return try {
            val response = RetrofitUtil.scheduleService.getFriendSchedule(
                "Bearer $token", memberId
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 공개된 개인 스케쥴 조회
    suspend fun getPublicSchedule(
        token: String,
        memberId: Long
    ): Result<List<FriendSchedule>> {
        return try {
            val response = RetrofitUtil.scheduleService.getPublicSchedule(
                "Bearer $token", memberId
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}