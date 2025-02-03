package com.ssafy.ganhoho.data.model.service

import com.ssafy.ganhoho.base.ApplicationClass

class RetrofilUtil {

    companion object {

        val memberService = ApplicationClass.retrofit.create(MemberService::class.java)


    }
}