package com.flagquest.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagquest.app.data.repository.CountryRepository
import com.flagquest.app.domain.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val isLoading: Boolean = true,
    val countries: List<Country> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repo: CountryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    init { load() }

    fun reload() { load() }

    private fun load() {
        viewModelScope.launch {
            _state.value = ExploreUiState(isLoading = true)
            try {
                repo.ensureLoaded()
                repo.getCountries().collect { countries ->
                    _state.value = ExploreUiState(
                        isLoading = false,
                        countries = countries.sortedBy { it.name }
                    )
                }
            } catch (e: Exception) {
                _state.value = ExploreUiState(
                    isLoading = false,
                    error = e.message ?: "Erreur de chargement"
                )
            }
        }
    }
}
