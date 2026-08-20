package com.arrterm.ui.radarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.radarr.RadarrMovie
import com.arrterm.data.remote.radarr.RadarrQueueItem
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.GlassCard
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.SectionHeader
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.UiState
import com.arrterm.ui.theme.BubbleSuccess
import com.arrterm.ui.theme.BubbleWarning
import com.arrterm.ui.theme.SkyBlueDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarrScreen(
    viewModel: RadarrViewModel,
    onGoToSettings: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Radarr") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder(
                serviceName = "RADARR",
                onGoToSettings = onGoToSettings,
                modifier = Modifier.padding(padding),
            )
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(
                message = s.message,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding),
            )
            is UiState.Success -> RadarrContent(
                movies = s.data.movies,
                queue = s.data.queue,
                onMovieClick = onMovieClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}

@Composable
private fun RadarrContent(
    movies: List<RadarrMovie>,
    queue: List<RadarrQueueItem>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (queue.isNotEmpty()) {
            item { SectionHeader("Queue (${queue.size})") }
            items(queue, key = { "q${it.id}" }) { QueueRow(it) }
        }
        item { SectionHeader("Library (${movies.size})") }
        items(movies, key = { it.id }) { MovieRow(it, onClick = { onMovieClick(it.id) }) }
    }
}

@Composable
private fun QueueRow(item: RadarrQueueItem) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { item.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    color = SkyBlueDeep,
                )
                Text("${item.progressPercent}%", style = MaterialTheme.typography.bodySmall)
            }
            item.timeleft?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MovieRow(movie: RadarrMovie, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    movie.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val (label, color) = when {
                movie.hasFile -> "Downloaded" to BubbleSuccess
                movie.monitored -> "Monitored" to BubbleWarning
                else -> "Unmonitored" to MaterialTheme.colorScheme.onSurfaceVariant
            }
            StatusBadge(label, color)
        }
    }
}
