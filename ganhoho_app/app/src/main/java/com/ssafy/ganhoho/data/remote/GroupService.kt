package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupInviteLinkResponse
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.LeaveGroupResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupService {

    // 그룹 생성
    @POST("api/groups")
    suspend fun addGroup(
        @Header("Authorization") token:String,
        @Body groupDto: GroupDto
    ): Response<GroupDto>

    // 그룹 초대 수락
    @POST("api/groups/{inviteLink}")
    suspend fun joinGroupByInviteCode(
        @Header("Authorization") token: String,
        @Path("inviteLink") inviteLink: String
    ): Response<Unit>

    // 그룹 목록 조회
    @GET("api/groups")
    suspend fun getGroups(
        @Header("Authorization") token: String,
    ): Response<List<GroupDto>>

    // 그룹 초대 링크 조회
    @GET("api/groups/link/{groupId}")
    suspend fun getGroupInviteLink(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int
    ): Response<GroupInviteLinkResponse>

    // 그룹원 리스트 조회
    @GET("api/groups/members/{groupId}")
    suspend fun getGroupMemberInfo(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int
    ): Response<List<GroupMemberResponse>>

    // 그룹원 월별 스케쥴 조회
    @GET("api/groups/schedules/{groupId}") // URL에는 {yearMonth} 제거
    suspend fun getEachMemberMonthlySchedule(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int,
        @Query("yearMonth") yearMonth: String
    ): Response<List<MemberMonthlyScheduleResponse>>


    // 그룹 탈퇴
    @DELETE("api/groups/{groupId}")
    suspend fun leaveGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int
    ): Response<LeaveGroupResponse>


}