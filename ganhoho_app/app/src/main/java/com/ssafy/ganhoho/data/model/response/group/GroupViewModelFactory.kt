package com.ssafy.ganhoho.data.model.response.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.ganhoho.repository.GroupRepository
import com.ssafy.ganhoho.viewmodel.GroupViewModel

// GroupRepository와 TokenManager를 주입을 위한

class GroupViewModelFactory(
    private val repository: GroupRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}