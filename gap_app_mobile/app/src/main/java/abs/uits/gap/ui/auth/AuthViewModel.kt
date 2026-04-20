package abs.uits.gap.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import abs.uits.gap.data.repository.AuthRepository
import abs.uits.gap.data.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val originalPhone: String, val receivedOtpCode: String) : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Format phone logic: ensure 998 prefix exactly once
            var cleaned = phone.replace(Regex("[^0-9]"), "")
            val smsPhone = when {
                cleaned.length == 9 -> "998$cleaned"
                cleaned.length == 12 && cleaned.startsWith("998") -> cleaned
                else -> cleaned // Fallback for other formats
            }

            val result = repository.requestOtp(smsPhone)
            result.onSuccess { response ->
                _authState.value = AuthState.OtpSent(smsPhone, response.otpCode)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Noma'lum xatolik")
            }
        }
    }

    fun verifyOtp(phone: String, otpCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.verifyOtp(phone, otpCode)
            result.onSuccess { response ->
                tokenStorage.saveToken(response.token)
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Noma'lum xatolik")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, tokenStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
