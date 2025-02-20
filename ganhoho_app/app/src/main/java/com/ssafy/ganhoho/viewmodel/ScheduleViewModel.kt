package com.ssafy.ganhoho.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.dto.schedule.FriendPublicSchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MySchedule
import com.ssafy.ganhoho.data.model.dto.schedule.MyScheduleRequest
import com.ssafy.ganhoho.data.model.dto.schedule.WorkScheduleDto
import com.ssafy.ganhoho.data.model.response.schedule.MyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.AddMyScheduleResponse
import com.ssafy.ganhoho.data.model.response.schedule.FriendPersonalResponse
import com.ssafy.ganhoho.data.model.response.schedule.MyWorkResponse
import com.ssafy.ganhoho.data.model.response.schedule.ScheduleUpdateResponse
import com.ssafy.ganhoho.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.log

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

    // 친구 근무 스케줄 조회
    private val _friendWorkSchedule = MutableStateFlow<Result<List<WorkScheduleDto>>?>(null)
    val friendWorkSchedule: StateFlow<Result<List<WorkScheduleDto>>?> = _friendWorkSchedule

    // 공개된 친구 스케줄 조회
    private val _friendPublicSchedule = MutableStateFlow<Result<FriendPersonalResponse>?>(null)
    val friendPublicSchedule: StateFlow<Result<FriendPersonalResponse>?> = _friendPublicSchedule


    // 내 근무 스케줄 조회
    fun getMyWorkSchedule(token: String) {
        viewModelScope.launch {
            val response = repository.getMyWorkSchedule(token)
            _myWorkSchedule.value = response
            if (response.isSuccess) {
                response
            } else {
                Log.e("API_ERROR", "서버 오류 발생: ${response.exceptionOrNull()?.message}")
                Result.failure(Exception("서버에서 일정을 불러오지 못했습니다. 잠시 후 다시 시도해주세요."))
            }
            Log.d("ScheduleViewModel", "📌 개인 근무 스케줄 API 응답: $response")
        }
    }

    // 내 근무 스케줄 수정
    fun updateMyWorkSchedule(token: String, workScheduleId: Long, request: WorkScheduleDto) {
        viewModelScope.launch {
            val response = repository.updateMyWorkSchedule(token, workScheduleId, request)
            if (response.isSuccess) {
                Log.d("ScheduleViewModel", "✅ 근무 일정 수정 성공: ${response.getOrNull()}")
            } else {
                Log.e("ScheduleViewModel", "❌ 근무 일정 수정 실패: ${response.exceptionOrNull()?.message}")
            }
        }
    }


    // 개인 스케줄 조회
    fun getMySchedule(token: String) {
        viewModelScope.launch {
            val response = repository.getMySchedule(token)
            Log.d("ScheduleViewModel", "📌 개인 스케줄 API 응답: $response")
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
            Log.d("update", "📌 서버 응답: ${response}, 요청 데이터: $request")

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
    fun getFriendWorkSchedule(token: String, memberId: Long) {
        viewModelScope.launch {
            _friendWorkSchedule.value = repository.getFriendWorkSchedule(token, memberId)
        }
    }

    // 공개된 개인 스케줄 조회
    fun getFriendPublicSchedule(token: String, memberId: Long) {
        viewModelScope.launch {
            _friendPublicSchedule.value = repository.getFriendPublicSchedule(token, memberId)
            Log.d("friend", "getFriendPublicSchedule: ${friendPublicSchedule.value}")
        }
    }

    // 개인 일정 삭제
    fun deleteMySchedule(token: String, scheduleId: Long) {
        viewModelScope.launch {
            val response = repository.removeMySchedule(token, scheduleId)
            _deleteMyScheduleResult.value = response
        }
    }

    fun setMyScheduleInit() {
        _editMyScheduleResult.value = null
    }
}
