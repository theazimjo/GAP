package abs.uits.gap.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import abs.uits.gap.core.network.GroupDetailDto
import abs.uits.gap.core.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GroupDetailState {
    object Loading : GroupDetailState()
    data class Success(val group: GroupDetailDto) : GroupDetailState()
    data class Error(val message: String) : GroupDetailState()
}

class GroupDetailViewModel(
    private val repository: GroupRepository,
    private val groupId: Int
) : ViewModel() {

    private val _detailState = MutableStateFlow<GroupDetailState>(GroupDetailState.Loading)
    val detailState: StateFlow<GroupDetailState> = _detailState.asStateFlow()

    init {
        fetchGroupDetail()
    }

    fun fetchGroupDetail() {
        _detailState.value = GroupDetailState.Loading
        viewModelScope.launch {
            val result = repository.getGroupDetail(groupId)
            if (result.isSuccess) {
                _detailState.value = GroupDetailState.Success(result.getOrThrow())
            } else {
                _detailState.value = GroupDetailState.Error(result.exceptionOrNull()?.message ?: "Xatolik")
            }
        }
    }
}

class GroupDetailViewModelFactory(
    private val repository: GroupRepository,
    private val groupId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupDetailViewModel(repository, groupId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
