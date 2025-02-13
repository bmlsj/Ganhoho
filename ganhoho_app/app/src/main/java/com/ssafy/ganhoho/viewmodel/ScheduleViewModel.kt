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

    // 개인 일정 삭제
    private val _deleteMyScheduleResult = MutableStateFlow<Result<ScheduleUpdateResponse>?>(null)
    val deleteMyScheduleResult: StateFlow<Result<ScheduleUpdateResponse>?> = _deleteMyScheduleResult

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
            val response = repository.getMySchedule(token) // API 호출
            response.onSuccess { scheduleResponse ->
                Log.d("ScheduleViewModel", "📌 새로 불러온 일정: $scheduleResponse")

                val existingData = _mySchedules.value.associateBy { it.scheduleId }

                _mySchedules.value = scheduleResponse.data.map { newItem ->
                    existingData[newItem.scheduleId]?.copy(
                        scheduleTitle = newItem.scheduleTitle,
                        scheduleColor = newItem.scheduleColor
                    ) ?: newItem
                } ?: emptyList()
            }.onFailure {
                Log.e("ScheduleViewModel", "❌ 일정 불러오기 실패: ${it.message}")
            }

            getMyWorkSchedule(token) // 🔥 근무 일정도 같이 불러오기
        }
    }

    // 개인 스케줄 수정
    fun updateSchedule(token: String, scheduleId: Long, request: MySchedule) {
        viewModelScope.launch {
            val response = repository.updateMySchedule(token, scheduleId, request)
            Log.d("update", "📌 서버 응답: $response, 요청 데이터: $request")

            _editMyScheduleResult.value = response
            if (response.isSuccess) {
                Log.d("update", "✅ 수정 성공! fetchMySchedules() 호출")

                // ✅ 기존 스케줄 리스트에서 해당 일정 찾아서 업데이트
                _mySchedules.value = _mySchedules.value.map { schedule ->
                    if (schedule.scheduleId == scheduleId) {
                        request // 업데이트된 데이터로 변경
                    } else {
                        schedule
                    }
                }
                fetchMySchedules(token) // 🔥 일정 다시 불러오기
            } else {
                Log.e("update", "❌ 수정 실패: ${response.exceptionOrNull()?.message}")
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

    // 개인 일정 삭제
    fun deleteMySchedule(token: String, scheduleId: Long) {
        viewModelScope.launch {
            val response = repository.removeMySchedule(token, scheduleId)
            _deleteMyScheduleResult.value = response
        }
    }
}
