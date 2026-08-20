package com.arrterm.ui.radarr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.radarr.RadarrApi
import com.arrterm.data.remote.radarr.RadarrSearchCommand
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class MovieDetail(
    val id: Int,
    val title: String,
    val year: Int,
    val overview: String,
    val status: String,
    val monitored: Boolean,
    val hasFile: Boolean,
    val sizeOnDisk: Long,
    val path: String,
    val posterPath: String?,
    val config: ServerConfig,
    val raw: JsonObject,
)

private fun JsonObject.toMovieDetail(config: ServerConfig): MovieDetail = MovieDetail(
    id = this["id"]?.jsonPrimitive?.intOrNull ?: 0,
    title = this["title"]?.jsonPrimitive?.contentOrNull ?: "",
    year = this["year"]?.jsonPrimitive?.intOrNull ?: 0,
    overview = this["overview"]?.jsonPrimitive?.contentOrNull ?: "",
    status = this["status"]?.jsonPrimitive?.contentOrNull ?: "",
    monitored = this["monitored"]?.jsonPrimitive?.booleanOrNull ?: false,
    hasFile = this["hasFile"]?.jsonPrimitive?.booleanOrNull ?: false,
    sizeOnDisk = this["sizeOnDisk"]?.jsonPrimitive?.longOrNull ?: 0L,
    path = this["path"]?.jsonPrimitive?.contentOrNull ?: "",
    posterPath = (this["images"] as? JsonArray)
        ?.mapNotNull { it.jsonObject }
        ?.firstOrNull { it["coverType"]?.jsonPrimitive?.contentOrNull == "poster" }
        ?.get("url")?.jsonPrimitive?.contentOrNull,
    config = config,
    raw = this,
)

sealed interface DetailAction {
    data object SearchStarted : DetailAction
    data object Deleted : DetailAction
    data class Error(val message: String) : DetailAction
}

class MovieDetailViewModel(
    private val repository: ServerConfigRepository,
    private val movieId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val state: StateFlow<UiState<MovieDetail>> = _state.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _events = MutableSharedFlow<DetailAction>()
    val events: SharedFlow<DetailAction> = _events.asSharedFlow()

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
                val raw = ApiClientFactory.create<RadarrApi>(config).getMovieRaw(movieId)
                _state.value = UiState.Success(raw.toMovieDetail(config))
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Failed to load movie")
            }
        }
    }

    fun searchForRelease() {
        val config = repository.get(ServiceType.RADARR)
        if (!config.isConfigured) return
        _busy.value = true
        viewModelScope.launch {
            try {
                ApiClientFactory.create<RadarrApi>(config)
                    .postCommand(RadarrSearchCommand(movieIds = listOf(movieId)))
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
        val config = repository.get(ServiceType.RADARR)
        if (!config.isConfigured) return
        viewModelScope.launch {
            try {
                val mutated = JsonObject(current.raw.toMutableMap().apply {
                    put("monitored", JsonPrimitive(!current.monitored))
                })
                val api = ApiClientFactory.create<RadarrApi>(config)
                val updated = api.updateMovieRaw(movieId, mutated)
                _state.value = UiState.Success(updated.toMovieDetail(config))
            } catch (t: Throwable) {
                _events.emit(DetailAction.Error(t.message ?: "Update failed"))
            }
        }
    }

    fun delete(deleteFiles: Boolean) {
        val config = repository.get(ServiceType.RADARR)
        if (!config.isConfigured) return
        _busy.value = true
        viewModelScope.launch {
            try {
                ApiClientFactory.create<RadarrApi>(config)
                    .deleteMovie(movieId, deleteFiles = deleteFiles, addImportExclusion = false)
                _events.emit(DetailAction.Deleted)
            } catch (t: Throwable) {
                _events.emit(DetailAction.Error(t.message ?: "Delete failed"))
            } finally {
                _busy.value = false
            }
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository, movieId: Int) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MovieDetailViewModel(repository, movieId) as T
        }
    }
}
