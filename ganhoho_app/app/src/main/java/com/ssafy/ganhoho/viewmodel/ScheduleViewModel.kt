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
import com.ssafy.ganhoho.data.model.response.schedule.ScheduleUpdateResponse
import com.ssafy.ganhoho.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel() : ViewModel() {

    private val repository = ScheduleRepository()

    // 내 근무 스케줄 조회
    private val _myWorkSchedule = MutableStateFlow<Result<List<WorkScheduleDto>>?>(null)
    val myWorkSchedule: StateFlow<Result<List<WorkScheduleDto>>?> = _myWorkSchedule

    // 개인 스케쥴 다시 불러오기
    private val _mySchedules = MutableStateFlow<List<MySchedule>>(emptyList())
    val mySchedules: StateFlow<List<MySchedule>> = _mySchedules

    // 개인 스케줄 조회
    private val _mySchedule = MutableStateFlow<Result<MyScheduleResponse>?>(null)
    val mySchedule: StateFlow<Result<MyScheduleResponse>?> = _mySchedule

    // 개인 스케쥴 추가 결과
    private val _addMyScheduleResult = MutableStateFlow<Result<AddMyScheduleResponse>?>(null)
    val addMyScheduleResult: StateFlow<Result<AddMyScheduleResponse>?> = _addMyScheduleResult

    // 개인 스케쥴 수정 결과
    private val _editMyScheduleResult = MutableStateFlow<Result<ScheduleUpdateResponse>?>(null)
    val editMyScheduleResult: StateFlow<Result<ScheduleUpdateResponse>?> = _editMyScheduleResult

    // 친구 스케줄 조회
    private val _friendSchedule = MutableStateFlow<Result<List<WorkScheduleDto>>?>(null)
    val friendSchedule: StateFlow<Result<List<WorkScheduleDto>>?> = _friendSchedule

    // 공개된 개인 스케줄 조회
    private val _publicSchedule = MutableStateFlow<Result<List<FriendSchedule>>?>(null)
    val publicSchedule: StateFlow<Result<List<FriendSchedule>>?> = _publicSchedule

    // 내 근무 스케줄 조회
    fun getMyWorkSchedule(token: String) {
        viewModelScope.launch {
            _myWorkSchedule.value = repository.getMyWorkSchedule(token)
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
            Log.d("ScheduleViewModel", "📌 개인 스케줄 API 응답: $response $token")
            _mySchedule.value = response
        }
    }

    // 개인 스케줄 및 근무 스케줄 다시 로드
    fun fetchMySchedules(token: String) {
        viewModelScope.launch {
            val result = try {
                val response = repository.getMySchedule(token)
                Log.d("ScheduleViewModel", "📌 API 응답: $response")
                Result.success(response)
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "❌ 일정 불러오기 실패: ${e.message}")
                Result.failure(e)
            }

            result.onSuccess { response ->
                val scheduleResponse = response.getOrNull()
                _mySchedules.value = scheduleResponse?.data ?: emptyList()
            }

            // 🔥 추가: 개인 일정 불러온 후, 근무 스케줄도 업데이트
            getMyWorkSchedule(token)
        }
    }

    // 일정 추가 결과 초기화 및 새로고침
    fun resetScheduleResult(token: String) {
        _addMyScheduleResult.value = null
        fetchMySchedules(token)  // 추가/수정 후, 데이터를 새로고침
    }


    // 개인 스케줄 수정
    fun updateSchedule(token: String, scheduleId: Long, request: MySchedule) {
        viewModelScope.launch {
            val response = repository.updateMySchedule(token, scheduleId, request)
            Log.d("update", "$response $token $request")
            _editMyScheduleResult.value = response

            if (response.isSuccess) {
                fetchMySchedules(token) // 일정 추가 후 자동 새로고침
            } else {
                Log.e("ScheduleViewModel", "❌ 일정 추가 실패: ${response.exceptionOrNull()?.message}")
            }
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
