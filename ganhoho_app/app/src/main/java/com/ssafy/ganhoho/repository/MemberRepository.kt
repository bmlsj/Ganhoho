package com.ssafy.ganhoho.repository

import com.ssafy.ganhoho.data.model.dto.member.MemberDto
import com.ssafy.ganhoho.data.model.response.handleResponse
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

}