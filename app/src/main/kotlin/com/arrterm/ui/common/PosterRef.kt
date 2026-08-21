package com.arrterm.ui.common

import com.arrterm.data.settings.ServerConfig

data class PosterRef(val url: String?, val apiKey: String?)

/**
 * Prefers the original external image (TMDB/TVDB/Fanart's own CDN, via `remoteUrl`) since
 * it's publicly reachable with no auth at all. Only falls back to the server's own local
 * copy (which needs the API key, and even then only works if the server treats this
 * connection as trusted/local — see ServerConfigRepository notes) when no remote URL exists.
 */
fun posterRef(localUrl: String?, remoteUrl: String?, config: ServerConfig): PosterRef =
    if (!remoteUrl.isNullOrBlank()) {
        PosterRef(url = remoteUrl, apiKey = null)
    } else {
        PosterRef(url = config.resolve(localUrl), apiKey = config.apiKey)
    }
