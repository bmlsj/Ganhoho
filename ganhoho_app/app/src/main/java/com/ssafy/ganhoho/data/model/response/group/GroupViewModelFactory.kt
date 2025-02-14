package com.ssafy.ganhoho.data.model.response.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.ganhoho.base.TokenManager
import com.ssafy.ganhoho.data.repository.GroupRepository
import com.ssafy.ganhoho.viewmodel.GroupViewModel

// GroupRepository와 TokenManager를 주입을 위한
class GroupViewModelFactory(
    private val repository: GroupRepository,
    private val tokenManager: TokenManager // TokenManager 추가
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
