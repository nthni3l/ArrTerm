package com.arrterm.ui.sonarr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.sonarr.SonarrApi
import com.arrterm.data.remote.sonarr.SonarrQueueItem
import com.arrterm.data.remote.sonarr.SonarrSeries
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SonarrData(
    val series: List<SonarrSeries>,
    val queue: List<SonarrQueueItem>,
    val config: ServerConfig,
)

class SonarrViewModel(private val repository: ServerConfigRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<SonarrData>>(UiState.Loading)
    val state: StateFlow<UiState<SonarrData>> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val config = repository.get(ServiceType.SONARR)
        if (!config.isConfigured) {
            _state.value = UiState.NotConfigured
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val api = ApiClientFactory.create<SonarrApi>(config)
                val series = api.getSeries()
                val queue = api.getQueue().records
                _state.value = UiState.Success(SonarrData(series, queue, config))
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Failed to load Sonarr data")
            }
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SonarrViewModel(repository) as T
        }
    }
}
