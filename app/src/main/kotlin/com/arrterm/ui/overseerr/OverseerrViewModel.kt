package com.arrterm.ui.overseerr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.overseerr.OverseerrApi
import com.arrterm.data.remote.overseerr.OverseerrRequest
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OverseerrViewModel(private val repository: ServerConfigRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<OverseerrRequest>>>(UiState.Loading)
    val state: StateFlow<UiState<List<OverseerrRequest>>> = _state.asStateFlow()

    private val _pendingActionIds = MutableStateFlow<Set<Int>>(emptySet())
    val pendingActionIds: StateFlow<Set<Int>> = _pendingActionIds.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val config = repository.get(ServiceType.OVERSEERR)
        if (!config.isConfigured) {
            _state.value = UiState.NotConfigured
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val api = ApiClientFactory.create<OverseerrApi>(config)
                val requests = api.getRequests(filter = "pending").results
                _state.value = UiState.Success(requests)
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Failed to load Overseerr requests")
            }
        }
    }

    fun approve(requestId: Int) = act(requestId) { api -> api.approveRequest(requestId) }

    fun decline(requestId: Int) = act(requestId) { api -> api.declineRequest(requestId) }

    private fun act(requestId: Int, call: suspend (OverseerrApi) -> Unit) {
        val config = repository.get(ServiceType.OVERSEERR)
        if (!config.isConfigured) return
        _pendingActionIds.value = _pendingActionIds.value + requestId
        viewModelScope.launch {
            try {
                call(ApiClientFactory.create(config))
                val current = (_state.value as? UiState.Success)?.data ?: emptyList()
                _state.value = UiState.Success(current.filterNot { it.id == requestId })
            } catch (_: Throwable) {
                // leave the item in place; user can retry the action
            } finally {
                _pendingActionIds.value = _pendingActionIds.value - requestId
            }
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                OverseerrViewModel(repository) as T
        }
    }
}
