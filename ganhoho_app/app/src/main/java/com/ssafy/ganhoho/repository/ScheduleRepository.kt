package com.ssafy.ganhoho.repository

import android.util.Log
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.data.model.dto.schedule.WorkScheduleDto
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.response.schedule.AddMyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendPersonalResponse
import com.ssafy.ganhoho.data.model.response.schedule.MyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.MyWorkResponse
import com.ssafy.ganhoho.data.model.response.schedule.ScheduleUpdateResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class ScheduleRepository {

    // 내 근무 스케쥴 조회
    suspend fun getMyWorkSchedule(
        token: String
    ): Result<List<WorkScheduleDto>> {
        return try {
            val response =
                RetrofitUtil.scheduleService.getMyWorkSchedule("Bearer $token")
            Log.d("Schedule", "📌 개인 근무 스케줄 API 응답: $response")
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
    ): Result<MyScheduleResponse> {
        return try {
            val response = RetrofitUtil.scheduleService.getMySchedule(
                "Bearer $token"
            )
            Log.d("ScheduleViewModel", "레포지토리 응답: ${response}")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 개인 스케쥴 수정
    suspend fun updateMySchedule(
        token: String,
        scheduleId: Long,
        request: MySchedule
    ): Result<ScheduleUpdateResponse> {
        return try {
            val response = RetrofitUtil.scheduleService.updateSchedule(
                "Bearer $token", scheduleId, request
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 개인 스케줄 삭제
    suspend fun removeMySchedule(
        token: String,
        scheduleId: Long
    ): Result<ScheduleUpdateResponse> {
        return try {
            val response = RetrofitUtil.scheduleService.removeMySchedule(
                "Bearer $token", scheduleId
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
    ): Result<AddMyScheduleResponse> {
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
    suspend fun getFriendWorkSchedule(
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
    suspend fun getFriendPublicSchedule(
        token: String,
        memberId: Long
    ): Result<FriendPersonalResponse> {
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