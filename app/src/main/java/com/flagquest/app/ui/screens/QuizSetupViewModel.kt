package com.flagquest.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagquest.app.data.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizSetupUiState(
    val isLoading: Boolean = true,
    val regionsMap: Map<String, List<String>> = emptyMap()
)

@HiltViewModel
class QuizSetupViewModel @Inject constructor(
    private val repo: CountryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizSetupUiState())
    val state: StateFlow<QuizSetupUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.ensureLoaded()
            val map = repo.getRegionsAndSubregions()
            _state.value = QuizSetupUiState(isLoading = false, regionsMap = map)
        }
    }
}
