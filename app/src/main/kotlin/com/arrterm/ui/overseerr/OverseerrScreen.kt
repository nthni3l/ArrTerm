package com.arrterm.ui.overseerr

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arrterm.data.remote.overseerr.OverseerrRequest
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.FullScreenError
import com.arrterm.ui.common.FullScreenLoading
import com.arrterm.ui.common.NotConfiguredPlaceholder
import com.arrterm.ui.common.PillButton
import com.arrterm.ui.common.PosterPlaceholder
import com.arrterm.ui.common.StatusBadge
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.common.ToastBus
import com.arrterm.ui.common.UiState
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.ApproveGreen
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextSecondary

@Composable
fun OverseerrScreen(
    viewModel: OverseerrViewModel,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val pendingActionIds by viewModel.pendingActionIds.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        val count = (state as? UiState.Success)?.data?.size ?: 0
        TabTopBar(title = "Overseerr", count = if (state is UiState.Success) "$count pending" else "")

        when (val s = state) {
            is UiState.NotConfigured -> NotConfiguredPlaceholder("OVERSEERR", onGoToSettings, Modifier.fillMaxSize())
            is UiState.Loading -> FullScreenLoading(Modifier.fillMaxSize())
            is UiState.Error -> FullScreenError(s.message, viewModel::refresh, Modifier.fillMaxSize())
            is UiState.Success -> OverseerrContent(
                requests = s.data,
                pendingActionIds = pendingActionIds,
                onApprove = { id -> viewModel.approve(id); ToastBus.show("Approved") },
                onDecline = { id -> viewModel.decline(id); ToastBus.show("Declined") },
                modifier = Modifier.fillMaxSize(),
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
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(text = "No pending requests", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        items(requests, key = { it.id }) { request ->
            RequestRow(
                request = request,
                isBusy = request.id in pendingActionIds,
                onApprove = { onApprove(request.id) },
                onDecline = { onDecline(request.id) },
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 10.dp))
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
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PosterPlaceholder(modifier = Modifier.size(44.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            request.media.mediaType.uppercase(),
                            color = AccentGreen,
                            fontFamily = JetBrainsMono,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            "#${request.media.tmdbId ?: request.media.id}",
                            color = TextMuted,
                            fontFamily = JetBrainsMono,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    Text(
                        "requested by ${request.requestedBy.displayName.ifBlank { "unknown" }}",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                StatusBadge(request.statusLabel, TextSecondary)
            }
            if (isBusy) {
                Text(
                    "PROCESSING…",
                    modifier = Modifier.padding(top = 12.dp),
                    color = TextMuted,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PillButton(text = "Approve", onClick = onApprove, tint = ApproveGreen, verticalPadding = 9.dp, modifier = Modifier.weight(1f))
                    PillButton(text = "Decline", onClick = onDecline, filled = false, tint = StatusError, verticalPadding = 9.dp, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
