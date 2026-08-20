package com.arrterm.data.remote.sonarr

import kotlinx.serialization.Serializable

@Serializable
data class SonarrImage(
    val coverType: String = "",
    val url: String? = null,
    val remoteUrl: String? = null,
)

@Serializable
data class SonarrSeries(
    val id: Int = 0,
    val title: String = "",
    val year: Int = 0,
    val monitored: Boolean = false,
    val status: String = "",
    val seasonCount: Int = 0,
    val statistics: SonarrSeriesStatistics = SonarrSeriesStatistics(),
    val images: List<SonarrImage> = emptyList(),
) {
    val posterPath: String?
        get() = images.firstOrNull { it.coverType == "poster" }?.url
}

@Serializable
data class SonarrSeriesStatistics(
    val episodeFileCount: Int = 0,
    val episodeCount: Int = 0,
)

@Serializable
data class SonarrQueueItem(
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
data class SonarrQueueResponse(
    val records: List<SonarrQueueItem> = emptyList(),
    val totalRecords: Int = 0,
)

@Serializable
data class SonarrSystemStatus(
    val appName: String = "",
    val version: String = "",
)

@Serializable
data class SonarrSearchCommand(
    val name: String = "SeriesSearch",
    val seriesId: Int,
)
