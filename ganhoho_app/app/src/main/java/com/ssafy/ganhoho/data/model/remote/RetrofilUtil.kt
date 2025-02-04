package com.ssafy.ganhoho.data.model.remote

import com.ssafy.ganhoho.base.ApplicationClass

class RetrofitUtil {

    companion object {

        val memberService = ApplicationClass.retrofit.create(MemberService::class.java)


    }
}