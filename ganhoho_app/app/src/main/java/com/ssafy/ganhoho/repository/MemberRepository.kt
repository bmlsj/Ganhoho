package com.ssafy.ganhoho.repository

import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.data.model.dto.member.UpdateHospitalWardRequest
import com.ssafy.ganhoho.data.model.response.handleResponse
import com.ssafy.ganhoho.data.model.response.member.MyPageResponse
import com.ssafy.ganhoho.data.remote.RetrofitUtil

class MemberRepository {

    // 회원 검색
    suspend fun searchFriend(token: String, friendLoginId: String):
            Result<List<MemberDto>> {
        return try {
            val response = RetrofitUtil.memberService.searchFriend("Bearer $token", friendLoginId)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 마이페이지 정보
    suspend fun getMyPageInfo(token: String): Result<MyPageResponse> {

        return try {
            val response = RetrofitUtil.memberService.getMyPageInfo("Bearer $token")
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원 병원/병동 정보 수정
    suspend fun updateHospitalAndWardInfo(
        token: String,
        request: UpdateHospitalWardRequest
    ): Result<MyPageResponse> {
        return try {
            val response = RetrofitUtil.memberService.updateHospitalAndWardInfo("Bearer $token", request)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}