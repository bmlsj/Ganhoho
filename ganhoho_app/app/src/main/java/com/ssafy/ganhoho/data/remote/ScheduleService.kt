package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.schedule.FriendPublicSchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.data.model.dto.schedule.WorkScheduleDto
import com.ssafy.ganhoho.data.model.response.schedule.MyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.AddMyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendPersonalResponse
import com.ssafy.ganhoho.data.model.response.schedule.MyWorkResponse
import com.ssafy.ganhoho.data.model.response.schedule.ScheduleUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScheduleService {

    // 내 근무 스케쥴 조회
    @GET("api/schedules/work")
    suspend fun getMyWorkSchedule(
        @Header("Authorization") token: String
    ): Response<List<WorkScheduleDto>>

    // 내 근무 스케쥴 수정
    @PUT("api/schedules/work/{workScheduleId}")
    suspend fun updateMyWorkSchedule(
        @Header("Authorization") token: String,
        @Path("workScheduleId") workScheduleId: Long,
        @Body request: WorkScheduleDto
    ): Response<ScheduleUpdateResponse>


    // 개인 스케쥴 조회
    @GET("api/schedules/personal")
    suspend fun getMySchedule(
        @Header("Authorization") token: String
    ): Response<MyScheduleResponse>

    // 개인 스케쥴 수정
    @PUT("api/schedules/personal/{scheduleId}")
    suspend fun updateSchedule(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Long,
        @Body request: MySchedule
    ): Response<ScheduleUpdateResponse>


    // 개인 스케쥴 추가
    @POST("api/schedules/personal")
    suspend fun addMySchedule(
        @Header("Authorization") token: String,
        @Body request: MyScheduleRequest
    ): Response<AddMyScheduleResponse>

    // 개인 스케줄 삭제
    @DELETE("api/schedules/personal/{scheduleId}")
    suspend fun removeMySchedule(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Long
    ) : Response<ScheduleUpdateResponse>

    // 친구 근무 스케쥴 조회
    @GET("api/schedules/work/{memberId}")
    suspend fun getFriendSchedule(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Long
    ): Response<List<WorkScheduleDto>>

    // 공개된 개인 스케쥴 조회
    @GET("api/schedules/personal/{memberId}")
    suspend fun getPublicSchedule(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Long
    ): Response<FriendPersonalResponse>

}