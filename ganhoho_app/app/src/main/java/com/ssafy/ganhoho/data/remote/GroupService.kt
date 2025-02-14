package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupInviteLinkResponse
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupService {

    // 그룹 생성
    @POST("api/groups")
    suspend fun addGroup(
        @Header("Authorization") token:String,
        @Body groupDto: GroupDto
    ): Response<GroupDto>

    // 그룹 초대 수락

    // 그룹 목록 조회
    @GET("api/groups")
    suspend fun getGroups(): List<GroupDto> // 데이터를 리스트 형태로 받아옴

    // 그룹 초대 링크 조회
    @GET("api/groups/link/{groupId}")
    suspend fun getGroupInviteLink(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long
    ): Response<GroupInviteLinkResponse>

    // 그룹원 정보 전체 조회
    @GET("api/groups/members/{groupId}")
    suspend fun getGroupMemberInfo(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long
    ): Response<List<GroupMemberResponse>>

    // 그룹원 월별 스케쥴 조회
    @GET("api/groups/schedules/{groupId}")
    suspend fun getEachMemberMonthlySchedule(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long,
        @Path("yearMonth") yearMonth: String
    ): Response<List<MemberMonthlyScheduleResponse>>

    // 그룹 탈퇴
    @DELETE("api/groups/{groupId}")
    suspend fun leaveGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Long
    ): Response<Boolean>


}