package com.arrterm.ui.radarr

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.radarr.RadarrMovie
import com.arrterm.data.remote.radarr.RadarrQueueItem
import com.arrterm.data.settings.ServerConfig
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.FilterChipRow
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.SearchField
import com.arrterm.ui.common.SectionLabel
import com.arrterm.ui.common.ServerImage
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.common.UiState
import com.arrterm.ui.common.posterRef
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.StatusSuccess
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary

private enum class MovieFilter(val label: String) {
    ALL("All"),
    DOWNLOADED("Downloaded"),
    MONITORED("Monitored"),
    UNMONITORED("Unmonitored"),
}

@Composable
fun RadarrScreen(
    viewModel: RadarrViewModel,
    onGoToSettings: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var filterIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        val count = (state as? UiState.Success)?.data?.movies?.size ?: 0
        TabTopBar(title = "Radarr", count = if (state is UiState.Success) "$count titles" else "")

        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder("RADARR", onGoToSettings, Modifier.fillMaxSize())
            is UiState.Loading -> FullScreenLoading(Modifier.fillMaxSize())
            is UiState.Error -> FullScreenError(s.message, viewModel::refresh, Modifier.fillMaxSize())
            is UiState.Success -> RadarrContent(
                movies = s.data.movies,
                queue = s.data.queue,
                config = s.data.config,
                query = query,
                onQueryChange = { query = it },
                filterIndex = filterIndex,
                onFilterChange = { filterIndex = it },
                onMovieClick = onMovieClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun RadarrContent(
    movies: List<RadarrMovie>,
    queue: List<RadarrQueueItem>,
    config: ServerConfig,
    query: String,
    onQueryChange: (String) -> Unit,
    filterIndex: Int,
    onFilterChange: (Int) -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filter = MovieFilter.entries[filterIndex]
    val filtered = movies.filter { movie ->
        (query.isBlank() || movie.title.contains(query, ignoreCase = true)) &&
            when (filter) {
                MovieFilter.ALL -> true
                MovieFilter.DOWNLOADED -> movie.hasFile
                MovieFilter.MONITORED -> movie.monitored
                MovieFilter.UNMONITORED -> !movie.monitored
            }
    }

    Column(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            SearchField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Search movies…",
                modifier = Modifier.fillMaxWidth(),
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 10.dp))
            FilterChipRow(
                labels = MovieFilter.entries.map { it.label },
                selectedIndex = filterIndex,
                onSelect = onFilterChange,
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            if (queue.isNotEmpty() && query.isBlank() && filter == MovieFilter.ALL) {
                item { SectionLabel("Queue (${queue.size})") }
                items(queue, key = { "q${it.id}" }) {
                    QueueRow(it)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
                }
            }
            item { SectionLabel("Library (${filtered.size})") }
            items(filtered, key = { it.id }) {
                MovieRow(it, config, onClick = { onMovieClick(it.id) })
                androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
            }
        }
    }
}

@Composable
private fun QueueRow(item: RadarrQueueItem) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            androidx.compose.material3.Text(item.title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    color = AccentGreen,
                    trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.1f),
                )
                androidx.compose.material3.Text(
                    "${item.progressPercent}%",
                    color = com.arrterm.ui.theme.TextSecondary,
                    fontFamily = com.arrterm.ui.theme.JetBrainsMono,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            item.timeleft?.let {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))
                androidx.compose.material3.Text(
                    "$it remaining",
                    color = TextMuted,
                    fontFamily = com.arrterm.ui.theme.JetBrainsMono,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun MovieRow(movie: RadarrMovie, config: ServerConfig, onClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val poster = posterRef(movie.posterImage?.url, movie.posterImage?.remoteUrl, config)
            ServerImage(
                url = poster.url,
                apiKey = poster.apiKey,
                modifier = Modifier.size(width = 44.dp, height = 64.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(
                    movie.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 3.dp))
                androidx.compose.material3.Text(
                    movie.year.toString(),
                    color = TextMuted,
                    fontFamily = com.arrterm.ui.theme.JetBrainsMono,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            val (label, color) = when {
                movie.hasFile -> "Downloaded" to StatusSuccess
                movie.monitored -> "Monitored" to AccentGreen
                else -> "Unmonitored" to TextMuted
            }
            StatusBadge(label, color)
        }
    }
}
