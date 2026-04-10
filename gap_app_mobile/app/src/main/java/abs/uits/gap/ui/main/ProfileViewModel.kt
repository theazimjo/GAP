package abs.uits.gap.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import abs.uits.gap.core.network.ProfileDto
import abs.uits.gap.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: ProfileDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    object Success : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}

class ProfileViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateState: StateFlow<UpdateProfileState> = _updateState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            val result = repository.getMe()
            if (result.isSuccess) {
                _profileState.value = ProfileState.Success(result.getOrThrow())
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Xatolik yuz berdi")
            }
        }
    }

    fun updateProfile(name: String) {
        _updateState.value = UpdateProfileState.Loading
        viewModelScope.launch {
            val result = repository.updateProfile(name)
            if (result.isSuccess) {
                _updateState.value = UpdateProfileState.Success
                fetchProfile() // Refresh profile
            } else {
                _updateState.value = UpdateProfileState.Error(result.exceptionOrNull()?.message ?: "Xatolik")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateProfileState.Idle
    }
}

class ProfileViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
