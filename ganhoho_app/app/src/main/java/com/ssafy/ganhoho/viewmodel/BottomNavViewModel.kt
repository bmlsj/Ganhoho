package com.ssafy.ganhoho.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

open class BottomNavViewModel : ViewModel() {
    private val _isBottomNavVisible = mutableStateOf(true)
    val isBottomNavVisible: State<Boolean> = _isBottomNavVisible

    fun showBottomNav() {
        _isBottomNavVisible.value = true
    }

    fun hideBottomNav() {
        _isBottomNavVisible.value = false
    }
}
