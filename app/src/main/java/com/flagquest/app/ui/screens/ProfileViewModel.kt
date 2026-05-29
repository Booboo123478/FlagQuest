package com.flagquest.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagquest.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val email: String? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val isSignedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileUiState(email = authRepo.currentUser?.email)
    )
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun updateProfile(newEmail: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, updateSuccess = false)
            try {
                val user = authRepo.currentUser ?: error("Non connecté")

                if (newEmail != user.email) {
                    authRepo.updateEmail(newEmail)
                }
                if (newPassword.isNotBlank()) {
                    authRepo.updatePassword(newPassword)
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    email = authRepo.currentUser?.email,
                    updateSuccess = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erreur lors de la mise à jour"
                )
            }
        }
    }

    fun signOut() {
        authRepo.signOut()
        _state.value = _state.value.copy(isSignedOut = true)
    }
}
