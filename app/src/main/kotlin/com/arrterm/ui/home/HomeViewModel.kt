package com.arrterm.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.overseerr.OverseerrApi
import com.arrterm.data.remote.radarr.RadarrApi
import com.arrterm.data.remote.sonarr.SonarrApi
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ServiceSummary {
    val service: ServiceType

    data class NotConfigured(override val service: ServiceType) : ServiceSummary
    data class Loading(override val service: ServiceType) : ServiceSummary
    data class Error(override val service: ServiceType, val message: String) : ServiceSummary
    data class Loaded(
        override val service: ServiceType,
        val primaryLabel: String,
        val secondaryLabel: String,
    ) : ServiceSummary
}

class HomeViewModel(private val repository: ServerConfigRepository) : ViewModel() {

    private val _summaries = MutableStateFlow(
        ServiceType.entries.associateWith { ServiceSummary.Loading(it) as ServiceSummary },
    )
    val summaries: StateFlow<Map<ServiceType, ServiceSummary>> = _summaries.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        ServiceType.entries.forEach { loadService(it) }
    }

    private fun loadService(service: ServiceType) {
        val config = repository.get(service)
        if (!config.isConfigured) {
            set(service, ServiceSummary.NotConfigured(service))
            return
        }
        set(service, ServiceSummary.Loading(service))
        viewModelScope.launch {
            try {
                val summary = when (service) {
                    ServiceType.RADARR -> {
                        val api = ApiClientFactory.create<RadarrApi>(config)
                        val movies = api.getMovies()
                        val queueCount = api.getQueue().totalRecords
                        ServiceSummary.Loaded(
                            service,
                            primaryLabel = "${movies.size} movies",
                            secondaryLabel = if (queueCount > 0) "$queueCount downloading" else "Queue is empty",
                        )
                    }
                    ServiceType.SONARR -> {
                        val api = ApiClientFactory.create<SonarrApi>(config)
                        val series = api.getSeries()
                        val queueCount = api.getQueue().totalRecords
                        ServiceSummary.Loaded(
                            service,
                            primaryLabel = "${series.size} series",
                            secondaryLabel = if (queueCount > 0) "$queueCount downloading" else "Queue is empty",
                        )
                    }
                    ServiceType.OVERSEERR -> {
                        val api = ApiClientFactory.create<OverseerrApi>(config)
                        val pending = api.getRequests(filter = "pending").results.size
                        ServiceSummary.Loaded(
                            service,
                            primaryLabel = if (pending > 0) "$pending pending" else "No pending requests",
                            secondaryLabel = "",
                        )
                    }
                }
                set(service, summary)
            } catch (t: Throwable) {
                set(service, ServiceSummary.Error(service, t.message ?: "Failed to load"))
            }
        }
    }

    private fun set(service: ServiceType, summary: ServiceSummary) {
        _summaries.value = _summaries.value.toMutableMap().apply { put(service, summary) }
    }

    companion object {
        fun factory(repository: ServerConfigRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(repository) as T
        }
    }
}
