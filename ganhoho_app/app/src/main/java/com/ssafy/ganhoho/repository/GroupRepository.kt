package com.ssafy.ganhoho.data.repository


import android.util.Log
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupInviteLinkResponse
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class GroupRepository {

    // 그룹 목록 조회
    suspend fun getGroupList(): Result<List<GroupDto>> {
        return try {
            val response = RetrofitUtil.groupService.getGroups()
            Result.success(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group list", e)
            Result.failure(e)
        }
    }
    
    // 그룹 초대 링크 조회
    suspend fun getGroupInviteLink(token: String, groupId: Long): Result<GroupInviteLinkResponse> {
        return try{
            val response = RetrofitUtil.groupService.getGroupInviteLink("Bearer $token", groupId)
            handleResponse(response)
        } catch (e: Exception){
            Log.e("GroupRepository", "Error feching group Invite Link", e)
            Result.failure(e)
        }
    }

    // 그룹원 정보 가져오기
    suspend fun getGroupMembers(token: String, groupId: Int): Result<List<GroupMemberResponse>> {
        return try {
            val response = RetrofitUtil.groupService.getGroupMemberInfo("Bearer $token", groupId.toLong())
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group members", e)
            Result.failure(e)
        }
    }

    // 그룹원 스케줄 가져오기
    suspend fun getMemberSchedules(token: String, groupId: Int, yearMonth: String): Result<List<MemberMonthlyScheduleResponse>> {
        return try {
            val response = RetrofitUtil.groupService.getEachMemberMonthlySchedule("Bearer $token", groupId, yearMonth)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching member schedules", e)
            Result.failure(e)
        }
    }
    suspend fun addGroup(token: String, groupName: String, groupIconType: Int): Result<GroupDto> {
        return try {
            val response = RetrofitUtil.groupService.addGroup(
                "Bearer $token",
                GroupDto(
                    groupId = 0, // 서버에서 자동 생성
                    groupName = groupName,
                    groupIconType = groupIconType,
                    groupMemberCount = 1 // 기본 1명 (본인)
                )
            )
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error adding group", e)
            Result.failure(e)
        }
    }


    // 그룹원 월별 스케줄 조회
    suspend fun getEachMemberMonthlySchedule(token: String, groupId: Int, yearMonth: String): Result<List<MemberMonthlyScheduleResponse>> {
        val formattedYearMonth = yearMonth  // "2025-02" -> "202502"

        val response = RetrofitUtil.groupService.getEachMemberMonthlySchedule(
            "Bearer $token", groupId, formattedYearMonth
        )

        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                return Result.success(responseBody)
            } else {
                return Result.failure(Exception("Empty response body"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("GroupRepository", "API Error Response: $errorBody")
            return Result.failure(Exception("API Request Failed: ${response.code()}"))
        }
    }


    // 그룹 탈퇴
    suspend fun leaveGroup(token: String, groupId: Int): Result<Boolean>{
        return try{
            val response = RetrofitUtil.groupService.leaveGroup("Bearer $token", groupId)
            handleResponse(response)
        }catch (e:Exception){
            Log.e("GroupRepository", "Error feching group leave group", e)
            Result.failure(e)
        }
    }
}
