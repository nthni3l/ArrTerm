package com.arrterm.ui.sonarr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.GlassButton
import com.arrterm.ui.common.GlassCard
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.UiState
import com.arrterm.ui.radarr.DetailAction
import com.arrterm.ui.theme.BubbleError
import com.arrterm.ui.theme.BubbleSuccess
import com.arrterm.ui.theme.SkyBlueDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    viewModel: SeriesDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val busy by viewModel.busy.collectAsState()
    var toast by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailAction.SearchStarted -> toast = "Search started"
                is DetailAction.Deleted -> onBack()
                is DetailAction.Error -> toast = event.message
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text((state as? UiState.Success)?.data?.title ?: "Series") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
            )
        },
    ) { padding ->
        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder("SONARR", onBack, Modifier.padding(padding))
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(s.message, viewModel::refresh, Modifier.padding(padding))
            is UiState.Success -> SeriesDetailContent(
                series = s.data,
                busy = busy,
                onToggleMonitored = viewModel::toggleMonitored,
                onSearch = viewModel::searchForRelease,
                onDeleteClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }

    if (toast != null) {
        AlertDialog(
            onDismissRequest = { toast = null },
            confirmButton = { GlassButton("OK", onClick = { toast = null }) },
            text = { Text(toast ?: "") },
        )
    }

    if (showDeleteDialog) {
        var deleteFiles by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove this series?") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = deleteFiles, onCheckedChange = { deleteFiles = it })
                    Text("Also delete the files from disk")
                }
            },
            confirmButton = {
                GlassButton(
                    text = "Delete",
                    tint = BubbleError,
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete(deleteFiles)
                    },
                )
            },
            dismissButton = {
                GlassButton(text = "Cancel", onClick = { showDeleteDialog = false })
            },
        )
    }
}

@Composable
private fun SeriesDetailContent(
    series: SeriesDetail,
    busy: Boolean,
    onToggleMonitored: () -> Unit,
    onSearch: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(series.title, style = MaterialTheme.typography.titleLarge)
                Text(series.year.toString(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(
                        text = "${series.episodeFileCount}/${series.episodeCount} episodes",
                        color = BubbleSuccess,
                    )
                    StatusBadge(
                        text = if (series.monitored) "Monitored" else "Unmonitored",
                        color = if (series.monitored) SkyBlueDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (series.overview.isNotBlank()) {
                    Text(series.overview, style = MaterialTheme.typography.bodyMedium)
                }
                if (series.path.isNotBlank()) {
                    Text(
                        series.path,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassButton(text = "Search for release", onClick = onSearch, enabled = !busy)
            GlassButton(
                text = if (series.monitored) "Unmonitor" else "Monitor",
                onClick = onToggleMonitored,
                enabled = !busy,
            )
        }
        GlassButton(text = "Delete", onClick = onDeleteClick, tint = BubbleError, enabled = !busy)

        if (busy) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
