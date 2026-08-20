package com.arrterm.data.settings

enum class ServiceType(val displayName: String) {
    RADARR("Radarr"),
    SONARR("Sonarr"),
    OVERSEERR("Overseerr"),
}

data class ServerConfig(
    val baseUrl: String = "",
    val apiKey: String = "",
) {
    val isConfigured: Boolean
        get() = baseUrl.isNotBlank() && apiKey.isNotBlank()

    /** baseUrl with no trailing slash, e.g. "http://192.168.1.50:7878" */
    val normalizedBaseUrl: String
        get() = baseUrl.trim().trimEnd('/')

    /** Resolves a server-relative resource path (e.g. a poster's "url" field) to a full URL. */
    fun resolve(path: String?): String? = path?.let { normalizedBaseUrl + it }
}
