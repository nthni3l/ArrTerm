package com.arrterm.data.remote.overseerr

import kotlinx.serialization.Serializable

@Serializable
data class OverseerrRequest(
    val id: Int = 0,
    val status: Int = 1, // 1=pending, 2=approved, 3=declined
    val media: OverseerrMedia = OverseerrMedia(),
    val requestedBy: OverseerrUser = OverseerrUser(),
    val createdAt: String = "",
) {
    val statusLabel: String
        get() = when (status) {
            1 -> "PENDING"
            2 -> "APPROVED"
            3 -> "DECLINED"
            else -> "UNKNOWN"
        }
}

@Serializable
data class OverseerrMedia(
    val id: Int = 0,
    val mediaType: String = "",
    val tmdbId: Int? = null,
    val status: Int = 1,
)

@Serializable
data class OverseerrUser(
    val id: Int = 0,
    val displayName: String = "",
)

@Serializable
data class OverseerrRequestPage(
    val results: List<OverseerrRequest> = emptyList(),
)

@Serializable
data class OverseerrStatus(
    val version: String = "",
)
