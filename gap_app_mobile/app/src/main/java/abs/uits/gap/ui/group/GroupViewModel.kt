package abs.uits.gap.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import abs.uits.gap.core.network.GroupDto
import abs.uits.gap.core.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GroupListState {
    object Loading : GroupListState()
    data class Success(val groups: List<GroupDto>) : GroupListState()
    data class Error(val message: String) : GroupListState()
}

sealed class CreateGroupState {
    object Idle : CreateGroupState()
    object Loading : CreateGroupState()
    object Success : CreateGroupState()
    data class Error(val message: String) : CreateGroupState()
}

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    private val _listState = MutableStateFlow<GroupListState>(GroupListState.Loading)
    val listState: StateFlow<GroupListState> = _listState.asStateFlow()

    private val _createState = MutableStateFlow<CreateGroupState>(CreateGroupState.Idle)
    val createState: StateFlow<CreateGroupState> = _createState.asStateFlow()

    init {
        fetchGroups()
    }

    fun fetchGroups() {
        _listState.value = GroupListState.Loading
        viewModelScope.launch {
            val result = repository.getGroups()
            if (result.isSuccess) {
                _listState.value = GroupListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _listState.value = GroupListState.Error(result.exceptionOrNull()?.message ?: "Guruhlarni yuklashda xatolik")
            }
        }
    }

    fun createGroup(
        name: String,
        emoji: String? = null,
        description: String? = null,
        isAmountOptional: Boolean = false,
        meetingDays: String? = null,
        selectionMethod: String = "random",
        totalPool: Double = 0.0,
        contributionAmount: Double = 0.0
    ) {
        _createState.value = CreateGroupState.Loading
        viewModelScope.launch {
            val result = repository.createGroup(
                name = name,
                emoji = emoji,
                description = description,
                isAmountOptional = isAmountOptional,
                meetingDays = meetingDays,
                selectionMethod = selectionMethod,
                totalPool = totalPool,
                contributionAmount = contributionAmount
            )
            if (result.isSuccess) {
                _createState.value = CreateGroupState.Success
                fetchGroups()
            } else {
                _createState.value = CreateGroupState.Error(result.exceptionOrNull()?.message ?: "Yaratishda xatolik")
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateGroupState.Idle
    }
}

class GroupViewModelFactory(private val repository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
