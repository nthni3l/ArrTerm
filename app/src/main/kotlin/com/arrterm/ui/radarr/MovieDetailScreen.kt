package com.arrterm.ui.radarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arrterm.ui.common.DeleteDialogController
import com.arrterm.ui.common.DetailTopBar
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.PillButton
import com.arrterm.ui.common.ServerImage
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.posterRef
import com.arrterm.ui.common.ToastBus
import com.arrterm.ui.common.UiState
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.StatusSuccess
import com.arrterm.ui.theme.StatusWarning
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.TextBody
import com.arrterm.ui.theme.TextMuted

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val busy by viewModel.busy.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailAction.SearchStarted -> ToastBus.show("Search started")
                is DetailAction.Deleted -> onBack()
                is DetailAction.Error -> ToastBus.show(event.message)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = (state as? UiState.Success)?.data?.title ?: "Movie", onBack = onBack)

        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder("RADARR", onBack, Modifier.fillMaxSize())
            is UiState.Loading -> FullScreenLoading(Modifier.fillMaxSize())
            is UiState.Error -> FullScreenError(s.message, viewModel::refresh, Modifier.fillMaxSize())
            is UiState.Success -> MovieDetailContent(
                movie = s.data,
                busy = busy,
                onToggleMonitored = viewModel::toggleMonitored,
                onSearch = viewModel::searchForRelease,
                onDeleteClick = {
                    DeleteDialogController.request(s.data.title) { deleteFiles -> viewModel.delete(deleteFiles) }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: MovieDetail,
    busy: Boolean,
    onToggleMonitored: () -> Unit,
    onSearch: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val poster = posterRef(movie.posterUrl, movie.posterRemoteUrl, movie.config)
        ServerImage(
            url = poster.url,
            apiKey = poster.apiKey,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = androidx.compose.ui.graphics.RectangleShape,
            placeholderLabel = "POSTER PLACEHOLDER",
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                movie.year.toString(),
                color = TextMuted,
                fontFamily = JetBrainsMono,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                StatusBadge(
                    text = if (movie.hasFile) "Downloaded" else "Missing",
                    color = if (movie.hasFile) StatusSuccess else StatusWarning,
                )
                StatusBadge(
                    text = if (movie.monitored) "Monitored" else "Unmonitored",
                    color = if (movie.monitored) AccentGreen else TextMuted,
                )
            }
            if (movie.overview.isNotBlank()) {
                Text(
                    movie.overview,
                    color = TextBody,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
            if (movie.path.isNotBlank()) {
                Text(
                    movie.path,
                    color = TextMuted,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 22.dp),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                PillButton(text = "Search for release", onClick = onSearch, enabled = !busy, modifier = Modifier.weight(1f))
                PillButton(
                    text = if (movie.monitored) "Unmonitor" else "Monitor",
                    onClick = onToggleMonitored,
                    filled = false,
                    enabled = !busy,
                    modifier = Modifier.weight(1f),
                )
            }
            PillButton(
                text = "Delete",
                onClick = onDeleteClick,
                filled = false,
                tint = StatusError,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth(),
            )

            if (busy) {
                Text(
                    "working…",
                    color = TextMuted,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
