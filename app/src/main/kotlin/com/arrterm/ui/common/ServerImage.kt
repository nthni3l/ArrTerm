package com.arrterm.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Shows a poster image, falling back to the striped placeholder when there's no URL or the
 * request fails. [apiKey] is only attached as an X-Api-Key header when non-null — pass null
 * for external CDN URLs (e.g. TMDB/TVDB's own `remoteUrl`) so the server's key isn't sent
 * to third parties; pass the key only when loading through the Radarr/Sonarr server itself.
 */
@Composable
fun ServerImage(
    url: String?,
    apiKey: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(6.dp),
    placeholderLabel: String = "IMG",
) {
    if (url.isNullOrBlank()) {
        PosterPlaceholder(modifier = modifier, shape = shape, label = placeholderLabel)
        return
    }

    val requestBuilder = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
    if (!apiKey.isNullOrBlank()) {
        requestBuilder.httpHeaders(NetworkHeaders.Builder().add("X-Api-Key", apiKey).build())
    }
    val request = requestBuilder.build()

    SubcomposeAsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier.clip(shape),
        contentScale = ContentScale.Crop,
    ) {
        val state = painter.state.value
        if (state is AsyncImagePainter.State.Error) {
            PosterPlaceholder(modifier = Modifier, shape = shape, label = placeholderLabel)
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}
