package com.arrterm.ui.radarr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.radarr.RadarrApi
import com.arrterm.data.remote.radarr.RadarrMovie
import com.arrterm.data.remote.radarr.RadarrQueueItem
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RadarrData(
    val movies: List<RadarrMovie>,
    val queue: List<RadarrQueueItem>,
    val config: ServerConfig,
)

class RadarrViewModel(private val repository: ServerConfigRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<RadarrData>>(UiState.Loading)
    val state: StateFlow<UiState<RadarrData>> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val config = repository.get(ServiceType.RADARR)
        if (!config.isConfigured) {
            _state.value = UiState.NotConfigured
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val api = ApiClientFactory.create<RadarrApi>(config)
                val movies = api.getMovies()
                val queue = api.getQueue().records
                _state.value = UiState.Success(RadarrData(movies, queue, config))
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Failed to load Radarr data")
            }
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RadarrViewModel(repository) as T
        }
    }
}
