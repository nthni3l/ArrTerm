package com.arrterm.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores the per-service {baseUrl, apiKey} pairs in EncryptedSharedPreferences, since an
 * API key is effectively a full-control credential to the user's media server.
 */
class ServerConfigRepository(context: Context) {

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "arrterm_server_config",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private val state = MutableStateFlow(loadAll())
    val configs: StateFlow<Map<ServiceType, ServerConfig>> = state.asStateFlow()

    fun get(service: ServiceType): ServerConfig = state.value[service] ?: ServerConfig()

    fun save(service: ServiceType, config: ServerConfig) {
        prefs.edit()
            .putString(urlKey(service), config.baseUrl.trim())
            .putString(keyKey(service), config.apiKey.trim())
            .apply()
        state.value = state.value.toMutableMap().apply { put(service, config) }
    }

    private fun loadAll(): Map<ServiceType, ServerConfig> =
        ServiceType.entries.associateWith { service ->
            ServerConfig(
                baseUrl = prefs.getString(urlKey(service), "") ?: "",
                apiKey = prefs.getString(keyKey(service), "") ?: "",
            )
        }

    private fun urlKey(service: ServiceType) = "${service.name}_base_url"
    private fun keyKey(service: ServiceType) = "${service.name}_api_key"
}
