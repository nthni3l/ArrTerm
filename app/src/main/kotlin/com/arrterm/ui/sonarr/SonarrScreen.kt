package com.arrterm.ui.sonarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.sonarr.SonarrQueueItem
import com.arrterm.data.remote.sonarr.SonarrSeries
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.data.settings.ServerConfig
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.SectionLabel
import com.arrterm.ui.common.ServerImage
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.common.UiState
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.StatusSuccess
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary
import com.arrterm.ui.theme.TextSecondary

@Composable
fun SonarrScreen(
    viewModel: SonarrViewModel,
    onGoToSettings: () -> Unit,
    onSeriesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        val count = (state as? UiState.Success)?.data?.series?.size ?: 0
        TabTopBar(title = "Sonarr", count = if (state is UiState.Success) "$count titles" else "")

        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder("SONARR", onGoToSettings, Modifier.fillMaxSize())
            is UiState.Loading -> FullScreenLoading(Modifier.fillMaxSize())
            is UiState.Error -> FullScreenError(s.message, viewModel::refresh, Modifier.fillMaxSize())
            is UiState.Success -> SonarrContent(
                series = s.data.series,
                queue = s.data.queue,
                config = s.data.config,
                onSeriesClick = onSeriesClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SonarrContent(
    series: List<SonarrSeries>,
    queue: List<SonarrQueueItem>,
    config: ServerConfig,
    onSeriesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        if (queue.isNotEmpty()) {
            item { SectionLabel("Queue (${queue.size})") }
            items(queue, key = { "q${it.id}" }) {
                QueueRow(it)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
            }
        }
        item { SectionLabel("Library (${series.size})") }
        items(series, key = { it.id }) {
            SeriesRow(it, config, onClick = { onSeriesClick(it.id) })
            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
        }
    }
}

@Composable
private fun QueueRow(item: SonarrQueueItem) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(item.title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    color = AccentGreen,
                    trackColor = Color.White.copy(alpha = 0.1f),
                )
                Text("${item.progressPercent}%", color = TextSecondary, fontFamily = JetBrainsMono, style = MaterialTheme.typography.labelMedium)
            }
            item.timeleft?.let {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))
                Text("$it remaining", color = TextMuted, fontFamily = JetBrainsMono, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun SeriesRow(series: SonarrSeries, config: ServerConfig, onClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ServerImage(
                url = config.resolve(series.posterPath),
                apiKey = config.apiKey,
                modifier = Modifier.size(width = 44.dp, height = 64.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(series.title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 3.dp))
                Text(
                    "${series.statistics.episodeFileCount}/${series.statistics.episodeCount} episodes",
                    color = TextMuted,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            val (label, color) = when {
                series.statistics.episodeCount > 0 && series.statistics.episodeFileCount >= series.statistics.episodeCount ->
                    "Complete" to StatusSuccess
                series.monitored -> "Monitored" to AccentGreen
                else -> "Unmonitored" to TextMuted
            }
            StatusBadge(label, color)
        }
    }
}
