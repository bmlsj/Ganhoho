package com.ssafy.ganhoho.data.remote

import com.ssafy.ganhoho.base.ApplicationClass

class RetrofitUtil {

    companion object {

        val authService = ApplicationClass.retrofit.create(AuthService::class.java)
        val friendService = ApplicationClass.retrofit.create(FriendService::class.java)
        val memberService = ApplicationClass.retrofit.create(MemberService::class.java)
        val groupService = ApplicationClass.retrofit.create(GroupService::class.java)

        val notiService = ApplicationClass.retrofit.create(NotiService::class.java)
        val scheduleService = ApplicationClass.retrofit.create(ScheduleService::class.java)

        // 지도
        val kakaoSearchApi = ApplicationClass.kakaoRetrofit.create(KakaoSearchService::class.java)
    }
}