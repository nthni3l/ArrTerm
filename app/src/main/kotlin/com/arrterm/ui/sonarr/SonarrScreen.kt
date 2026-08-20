package com.arrterm.ui.sonarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.sonarr.SonarrQueueItem
import com.arrterm.data.remote.sonarr.SonarrSeries
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.SectionHeader
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SonarrScreen(
    viewModel: SonarrViewModel,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("SONARR", fontFamily = FontFamily.Monospace) }) },
    ) { padding ->
        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder(
                serviceName = "SONARR",
                onGoToSettings = onGoToSettings,
                modifier = Modifier.padding(padding),
            )
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(
                message = s.message,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding),
            )
            is UiState.Success -> SonarrContent(
                series = s.data.series,
                queue = s.data.queue,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}

@Composable
private fun SonarrContent(
    series: List<SonarrSeries>,
    queue: List<SonarrQueueItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (queue.isNotEmpty()) {
            item { SectionHeader("QUEUE (${queue.size})") }
            items(queue, key = { "q${it.id}" }) { QueueRow(it) }
        }
        item { SectionHeader("LIBRARY (${series.size})") }
        items(series, key = { it.id }) { SeriesRow(it) }
    }
}

@Composable
private fun QueueRow(item: SonarrQueueItem) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(item.title, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { item.progressPercent / 100f },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            Text("${item.progressPercent}%", style = MaterialTheme.typography.bodySmall)
        }
        item.timeleft?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Composable
private fun SeriesRow(series: SonarrSeries) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(series.title, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${series.statistics.episodeFileCount}/${series.statistics.episodeCount} episodes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val (label, color) = when {
            series.statistics.episodeCount > 0 && series.statistics.episodeFileCount >= series.statistics.episodeCount ->
                "COMPLETE" to MaterialTheme.colorScheme.primary
            series.monitored -> "MONITORED" to MaterialTheme.colorScheme.secondary
            else -> "UNMONITORED" to MaterialTheme.colorScheme.onSurfaceVariant
        }
        StatusBadge(label, color)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}
