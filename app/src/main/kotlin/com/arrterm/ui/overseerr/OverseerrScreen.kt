package com.arrterm.ui.overseerr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.overseerr.OverseerrRequest
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverseerrScreen(
    viewModel: OverseerrViewModel,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val pendingActionIds by viewModel.pendingActionIds.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("OVERSEERR", fontFamily = FontFamily.Monospace) }) },
    ) { padding ->
        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder(
                serviceName = "OVERSEERR",
                onGoToSettings = onGoToSettings,
                modifier = Modifier.padding(padding),
            )
            is UiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is UiState.Error -> FullScreenError(
                message = s.message,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding),
            )
            is UiState.Success -> OverseerrContent(
                requests = s.data,
                pendingActionIds = pendingActionIds,
                onApprove = viewModel::approve,
                onDecline = viewModel::decline,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}

@Composable
private fun OverseerrContent(
    requests: List<OverseerrRequest>,
    pendingActionIds: Set<Int>,
    onApprove: (Int) -> Unit,
    onDecline: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (requests.isEmpty()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "// no pending requests",
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }
    LazyColumn(modifier = modifier) {
        items(requests, key = { it.id }) { request ->
            RequestRow(
                request = request,
                isBusy = request.id in pendingActionIds,
                onApprove = { onApprove(request.id) },
                onDecline = { onDecline(request.id) },
            )
        }
    }
}

@Composable
private fun RequestRow(
    request: OverseerrRequest,
    isBusy: Boolean,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${request.media.mediaType.uppercase()} #${request.media.tmdbId ?: request.media.id}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "requested by ${request.requestedBy.displayName.ifBlank { "unknown" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusBadge(request.statusLabel, MaterialTheme.colorScheme.secondary)
        }
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isBusy) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Button(onClick = onApprove) { Text("APPROVE") }
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("DECLINE") }
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}
