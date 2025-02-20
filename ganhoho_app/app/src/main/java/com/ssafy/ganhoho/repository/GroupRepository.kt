package com.ssafy.ganhoho.repository


import android.util.Log
import com.ssafy.ganhoho.data.model.dto.group.GroupDto
import com.ssafy.ganhoho.data.model.response.group.GroupInviteLinkResponse
import com.ssafy.ganhoho.data.model.response.group.GroupMemberResponse
import com.ssafy.ganhoho.data.model.response.group.MemberMonthlyScheduleResponse
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class GroupRepository {

    // 그룹 목록 조회
    suspend fun getGroupList(token: String): Result<List<GroupDto>> {
        return try {
            val response = RetrofitUtil.groupService.getGroups("Bearer $token")
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group list", e)
            Result.failure(e)
        }
    }


    // 그룹 초대 링크 조회
    suspend fun getGroupInviteLink(token: String, groupId: Int): Result<GroupInviteLinkResponse> {
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
            val response = RetrofitUtil.groupService.getGroupMemberInfo("Bearer $token", groupId)
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

    // 그룹 탈퇴
    suspend fun leaveGroup(token: String, groupId: Int): Result<Boolean> {
        return try {
            val response = RetrofitUtil.groupService.leaveGroup("Bearer $token", groupId)

            if (response.isSuccessful) {
                val success = response.body()?.success ?: false // DTO 사용하여 success 값 가져오기
                Log.d("GroupRepository", "그룹 탈퇴 성공: $success")
                Result.success(success)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupRepository", "그룹 탈퇴 실패: ${response.code()} - $errorBody")
                Result.failure(Exception("API Request Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "그룹 탈퇴 요청 중 오류 발생", e)
            Result.failure(e)
        }
    }

    // 초대 링크로 그룹 가입
    suspend fun joinGroupByInviteCode(token: String, inviteLink: String): Result<String> {
        return try {
            val response = RetrofitUtil.groupService.joinGroupByInviteCode("Bearer $token", inviteLink)
            if (response.isSuccessful) {
                Log.d("GroupRepository", "초대 수락 성공, groupId: $inviteLink")
                Result.success(inviteLink)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupRepository", "초대 수락 실패: ${response.code()} - $errorBody")
                Result.failure(Exception("초대 코드가 유효하지 않음"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "초대 코드 요청 중 오류 발생", e)
            Result.failure(e)
        }
    }

}
