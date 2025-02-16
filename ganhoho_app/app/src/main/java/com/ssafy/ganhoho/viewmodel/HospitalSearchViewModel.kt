package com.ssafy.ganhoho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ganhoho.data.model.response.auth.SearchResultItem
import com.ssafy.ganhoho.repository.HospitalSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HospitalSearchViewModel : ViewModel() {

    private val repository = HospitalSearchRepository()

    private val _searchResults = MutableStateFlow<List<SearchResultItem>?>(null)
    val searchResults: StateFlow<List<SearchResultItem>?> get() = _searchResults

    fun searchHospitals(query: String, x: Double, y: Double) {
        viewModelScope.launch {
            val result = repository.searchHospital(query, x.toString(), y.toString())
            result.onSuccess { hospitals ->
                _searchResults.value = hospitals
            }.onFailure {
                _searchResults.value = emptyList()
            }
        }
    }
}
