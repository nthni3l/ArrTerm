package com.arrterm.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrterm.data.remote.ApiClientFactory
import com.arrterm.data.remote.overseerr.OverseerrApi
import com.arrterm.data.remote.radarr.RadarrApi
import com.arrterm.data.remote.sonarr.SonarrApi
import com.arrterm.data.settings.ServerConfig
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.data.settings.ServiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TestState { IDLE, TESTING, SUCCESS, FAILURE }

class SettingsViewModel(private val repository: ServerConfigRepository) : ViewModel() {

    val configs: StateFlow<Map<ServiceType, ServerConfig>> = repository.configs

    private val _testStates = MutableStateFlow(ServiceType.entries.associateWith { TestState.IDLE })
    val testStates: StateFlow<Map<ServiceType, TestState>> = _testStates.asStateFlow()

    private val _testMessages = MutableStateFlow<Map<ServiceType, String>>(emptyMap())
    val testMessages: StateFlow<Map<ServiceType, String>> = _testMessages.asStateFlow()

    fun save(service: ServiceType, config: ServerConfig) {
        repository.save(service, config)
        ApiClientFactory.invalidate(config)
        setTestState(service, TestState.IDLE, null)
    }

    fun testConnection(service: ServiceType, config: ServerConfig) {
        if (!config.isConfigured) {
            setTestState(service, TestState.FAILURE, "Enter a URL and API key first")
            return
        }
        setTestState(service, TestState.TESTING, null)
        viewModelScope.launch {
            try {
                val message = when (service) {
                    ServiceType.RADARR -> {
                        val status = ApiClientFactory.create<RadarrApi>(config).getSystemStatus()
                        "Connected: ${status.appName} ${status.version}"
                    }
                    ServiceType.SONARR -> {
                        val status = ApiClientFactory.create<SonarrApi>(config).getSystemStatus()
                        "Connected: ${status.appName} ${status.version}"
                    }
                    ServiceType.OVERSEERR -> {
                        val status = ApiClientFactory.create<OverseerrApi>(config).getStatus()
                        "Connected: Overseerr ${status.version}"
                    }
                }
                setTestState(service, TestState.SUCCESS, message)
            } catch (t: Throwable) {
                setTestState(service, TestState.FAILURE, t.message ?: "Connection failed")
            }
        }
    }

    private fun setTestState(service: ServiceType, state: TestState, message: String?) {
        _testStates.value = _testStates.value.toMutableMap().apply { put(service, state) }
        _testMessages.value = _testMessages.value.toMutableMap().apply {
            if (message != null) put(service, message) else remove(service)
        }
    }

    companion object {
        fun factory(repository: ServerConfigRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SettingsViewModel(repository) as T
        }
    }
}
