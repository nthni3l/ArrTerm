package com.arrterm.ui.sonarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.sonarr.SonarrQueueItem
import com.arrterm.data.remote.sonarr.SonarrSeries
import com.arrterm.data.settings.ServerConfig
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.FilterChipRow
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.SearchField
import com.arrterm.ui.common.ServerImage
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.common.UiState
import com.arrterm.ui.common.posterRef
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.StatusSuccess
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary
import com.arrterm.ui.theme.TextSecondary

private enum class SonarrSubTab { LIBRARY, QUEUE }

private enum class SeriesFilter(val label: String) {
    ALL("All"),
    COMPLETE("Complete"),
    MONITORED("Monitored"),
    UNMONITORED("Unmonitored"),
}

@Composable
fun SonarrScreen(
    viewModel: SonarrViewModel,
    onGoToSettings: () -> Unit,
    onSeriesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var filterIndex by rememberSaveable { mutableIntStateOf(0) }
    var subTabIndex by rememberSaveable { mutableIntStateOf(0) }

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
                query = query,
                onQueryChange = { query = it },
                filterIndex = filterIndex,
                onFilterChange = { filterIndex = it },
                subTabIndex = subTabIndex,
                onSubTabChange = { subTabIndex = it },
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
    query: String,
    onQueryChange: (String) -> Unit,
    filterIndex: Int,
    onFilterChange: (Int) -> Unit,
    subTabIndex: Int,
    onSubTabChange: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val subTab = SonarrSubTab.entries[subTabIndex]
    val filter = SeriesFilter.entries[filterIndex]
    val filtered = series.filter { s ->
        (query.isBlank() || s.title.contains(query, ignoreCase = true)) &&
            when (filter) {
                SeriesFilter.ALL -> true
                SeriesFilter.COMPLETE -> s.statistics.episodeCount > 0 && s.statistics.episodeFileCount >= s.statistics.episodeCount
                SeriesFilter.MONITORED -> s.monitored
                SeriesFilter.UNMONITORED -> !s.monitored
            }
    }

    Column(modifier = modifier) {
        FilterChipRow(
            labels = listOf("Library (${series.size})", "Queue (${queue.size})"),
            selectedIndex = subTabIndex,
            onSelect = onSubTabChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        when (subTab) {
            SonarrSubTab.LIBRARY -> {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    SearchField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = "Search series…",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 10.dp))
                    FilterChipRow(
                        labels = SeriesFilter.entries.map { it.label },
                        selectedIndex = filterIndex,
                        onSelect = onFilterChange,
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(filtered, key = { it.id }) {
                        SeriesRow(it, config, onClick = { onSeriesClick(it.id) })
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
                    }
                }
            }
            SonarrSubTab.QUEUE -> {
                if (queue.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Queue is empty", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        items(queue, key = { it.id }) {
                            QueueRow(it)
                            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
                        }
                    }
                }
            }
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
            val poster = posterRef(series.posterImage?.url, series.posterImage?.remoteUrl, config)
            ServerImage(
                url = poster.url,
                apiKey = poster.apiKey,
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
