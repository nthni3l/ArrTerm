package com.arrterm.ui.sonarr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.sonarr.SonarrApi
import com.arrterm.data.remote.sonarr.SonarrSearchCommand
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.UiState
import com.arrterm.ui.radarr.DetailAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class SeriesDetail(
    val id: Int,
    val title: String,
    val year: Int,
    val overview: String,
    val status: String,
    val monitored: Boolean,
    val episodeFileCount: Int,
    val episodeCount: Int,
    val path: String,
    val raw: JsonObject,
)

private fun JsonObject.toSeriesDetail(): SeriesDetail {
    val stats = this["statistics"]?.jsonObject
    return SeriesDetail(
        id = this["id"]?.jsonPrimitive?.intOrNull ?: 0,
        title = this["title"]?.jsonPrimitive?.contentOrNull ?: "",
        year = this["year"]?.jsonPrimitive?.intOrNull ?: 0,
        overview = this["overview"]?.jsonPrimitive?.contentOrNull ?: "",
        status = this["status"]?.jsonPrimitive?.contentOrNull ?: "",
        monitored = this["monitored"]?.jsonPrimitive?.booleanOrNull ?: false,
        episodeFileCount = stats?.get("episodeFileCount")?.jsonPrimitive?.intOrNull ?: 0,
        episodeCount = stats?.get("episodeCount")?.jsonPrimitive?.intOrNull ?: 0,
        path = this["path"]?.jsonPrimitive?.contentOrNull ?: "",
        raw = this,
    )
}

class SeriesDetailViewModel(
    private val repository: ServerConfigRepository,
    private val seriesId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<SeriesDetail>>(UiState.Loading)
    val state: StateFlow<UiState<SeriesDetail>> = _state.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _events = MutableSharedFlow<DetailAction>()
    val events: SharedFlow<DetailAction> = _events.asSharedFlow()

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
                val raw = ApiClientFactory.create<SonarrApi>(config).getSeriesRaw(seriesId)
                _state.value = UiState.Success(raw.toSeriesDetail())
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Failed to load series")
            }
        }
    }

    fun searchForRelease() {
        val config = repository.get(ServiceType.SONARR)
        if (!config.isConfigured) return
        _busy.value = true
        viewModelScope.launch {
            try {
                ApiClientFactory.create<SonarrApi>(config)
                    .postCommand(SonarrSearchCommand(seriesId = seriesId))
                _events.emit(DetailAction.SearchStarted)
            } catch (t: Throwable) {
                _events.emit(DetailAction.Error(t.message ?: "Search failed"))
            } finally {
                _busy.value = false
            }
        }
    }

    fun toggleMonitored() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        val config = repository.get(ServiceType.SONARR)
        if (!config.isConfigured) return
        viewModelScope.launch {
            try {
                val mutated = JsonObject(current.raw.toMutableMap().apply {
                    put("monitored", JsonPrimitive(!current.monitored))
                })
                val api = ApiClientFactory.create<SonarrApi>(config)
                val updated = api.updateSeriesRaw(seriesId, mutated)
                _state.value = UiState.Success(updated.toSeriesDetail())
            } catch (t: Throwable) {
                _events.emit(DetailAction.Error(t.message ?: "Update failed"))
            }
        }
    }

    fun delete(deleteFiles: Boolean) {
        val config = repository.get(ServiceType.SONARR)
        if (!config.isConfigured) return
        _busy.value = true
        viewModelScope.launch {
            try {
                ApiClientFactory.create<SonarrApi>(config)
                    .deleteSeries(seriesId, deleteFiles = deleteFiles, addImportListExclusion = false)
                _events.emit(DetailAction.Deleted)
            } catch (t: Throwable) {
                _events.emit(DetailAction.Error(t.message ?: "Delete failed"))
            } finally {
                _busy.value = false
            }
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository, seriesId: Int) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SeriesDetailViewModel(repository, seriesId) as T
        }
    }
}
