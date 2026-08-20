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
 * Shows a poster image served through the configured Radarr/Sonarr instance (authenticated
 * with the same X-Api-Key as the rest of the API), falling back to the striped placeholder
 * when there's no poster URL or the request fails.
 */
@Composable
fun ServerImage(
    url: String?,
    apiKey: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(6.dp),
    placeholderLabel: String = "IMG",
) {
    if (url.isNullOrBlank()) {
        PosterPlaceholder(modifier = modifier, shape = shape, label = placeholderLabel)
        return
    }

    val request = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .httpHeaders(NetworkHeaders.Builder().add("X-Api-Key", apiKey).build())
        .crossfade(true)
        .build()

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
