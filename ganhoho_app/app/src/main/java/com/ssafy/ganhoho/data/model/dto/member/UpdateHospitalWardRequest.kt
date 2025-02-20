package com.ssafy.ganhoho.data.model.dto.member

data class UpdateHospitalWardRequest(
    val hospital: String? = "",  // nullable로 설정
    val ward: String? = "",
    val hospitalLat: Double?,
    val hospitalLng: Double?
)
