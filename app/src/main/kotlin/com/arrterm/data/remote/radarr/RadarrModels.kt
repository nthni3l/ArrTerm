package com.arrterm.data.remote.radarr

import kotlinx.serialization.Serializable

@Serializable
data class RadarrImage(
    val coverType: String = "",
    val url: String? = null,
    val remoteUrl: String? = null,
)

@Serializable
data class RadarrMovie(
    val id: Int = 0,
    val title: String = "",
    val year: Int = 0,
    val monitored: Boolean = false,
    val hasFile: Boolean = false,
    val status: String = "",
    val sizeOnDisk: Long = 0,
    val images: List<RadarrImage> = emptyList(),
) {
    val posterImage: RadarrImage?
        get() = images.firstOrNull { it.coverType == "poster" }
}

@Serializable
data class RadarrQueueItem(
    val id: Int = 0,
    val title: String = "",
    val status: String = "",
    val trackedDownloadStatus: String = "",
    val size: Double = 0.0,
    val sizeleft: Double = 0.0,
    val timeleft: String? = null,
) {
    val progressPercent: Int
        get() = if (size <= 0.0) 0 else (((size - sizeleft) / size) * 100).toInt().coerceIn(0, 100)
}

@Serializable
data class RadarrQueueResponse(
    val records: List<RadarrQueueItem> = emptyList(),
    val totalRecords: Int = 0,
)

@Serializable
data class RadarrSystemStatus(
    val appName: String = "",
    val version: String = "",
)

@Serializable
data class RadarrSearchCommand(
    val name: String = "MoviesSearch",
    val movieIds: List<Int>,
)
