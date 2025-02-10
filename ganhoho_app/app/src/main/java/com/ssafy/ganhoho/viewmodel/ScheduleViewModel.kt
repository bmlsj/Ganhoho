package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.group.WorkScheduleDto
import com.ssafy.ganhoho.data.model.dto.schedule.FriendSchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.data.model.response.schedule.MyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.AddMyScheduleResponse
import com.ssafy.ganhoho.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel() : ViewModel() {

    private val repository = ScheduleRepository()

    // 내 근무 스케줄 조회
    private val _myWorkSchedule = MutableStateFlow<Result<List<WorkScheduleDto>>?>(null)
    val myWorkSchedule: StateFlow<Result<List<WorkScheduleDto>>?> = _myWorkSchedule

    // 개인 스케줄 조회
    private val _mySchedule = MutableStateFlow<Result<MyScheduleResponse>?>(null)
    val mySchedule: StateFlow<Result<MyScheduleResponse>?> = _mySchedule

    // 개인 스케쥴 추가 결과
    private val _addMyScheduleResult = MutableStateFlow<Result<AddMyScheduleResponse>?>(null)
    val addMyScheduleResult: StateFlow<Result<AddMyScheduleResponse>?> = _addMyScheduleResult

    // 친구 스케줄 조회
    private val _friendSchedule = MutableStateFlow<Result<List<WorkScheduleDto>>?>(null)
    val friendSchedule: StateFlow<Result<List<WorkScheduleDto>>?> = _friendSchedule

    // 공개된 개인 스케줄 조회
    private val _publicSchedule = MutableStateFlow<Result<List<FriendSchedule>>?>(null)
    val publicSchedule: StateFlow<Result<List<FriendSchedule>>?> = _publicSchedule

    // 내 근무 스케줄 조회
    fun getMyWorkSchedule(token: String, yearMonth: String) {
        viewModelScope.launch {
            _myWorkSchedule.value = repository.getMyWorkSchedule(token, yearMonth)
        }
    }

    // 내 근무 스케줄 수정
    fun updateMyWorkSchedule(token: String, workScheduleId: Long, request: WorkScheduleDto) {
        viewModelScope.launch {
            repository.updateMyWorkSchedule(token, workScheduleId, request)
        }
    }

    // 개인 스케줄 조회
    fun getMySchedule(token: String) {
        viewModelScope.launch {
            val response = repository.getMySchedule(token)
            Log.d("ScheduleViewModel", "📌 개인 스케줄 API 응답: ${response} $token")
            _mySchedule.value = response
        }
    }

    // 개인 스케줄 수정
    fun updateSchedule(token: String, scheduleId: Long, request: MySchedule) {
        viewModelScope.launch {
            repository.updateSchedule(token, scheduleId, request)
        }
    }

    // 개인 스케줄 추가
    fun addMySchedule(token: String, request: MyScheduleRequest) {
        viewModelScope.launch {
            val response = repository.addMySchedule(token, request)
            _addMyScheduleResult.value = response
        }
    }

    // 일정 추가 결과 초기화 (다시 초기 상태로 돌리기)
    fun resetScheduleResult() {
        _addMyScheduleResult.value = null
    }

    // 친구 근무 스케줄 조회
    fun getFriendSchedule(token: String, memberId: Long) {
        viewModelScope.launch {
            _friendSchedule.value = repository.getFriendSchedule(token, memberId)
        }
    }

    // 공개된 개인 스케줄 조회
    fun getPublicSchedule(token: String, memberId: Long) {
        viewModelScope.launch {
            _publicSchedule.value = repository.getPublicSchedule(token, memberId)
        }
    }
}
