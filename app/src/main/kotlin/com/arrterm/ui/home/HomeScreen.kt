package com.arrterm.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arrterm.data.settings.ServiceType
import com.arrterm.ui.common.AppCard
import com.arrterm.ui.common.PillButton
import com.arrterm.ui.common.TabTopBar
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary
import com.arrterm.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onServiceClick: (ServiceType) -> Unit,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summaries by viewModel.summaries.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TabTopBar(title = "Home", count = "")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(ServiceType.entries) { service ->
                ServiceSummaryCard(
                    summary = summaries[service],
                    onClick = { onServiceClick(service) },
                    onGoToSettings = onGoToSettings,
                )
            }
        }
    }
}

@Composable
private fun ServiceSummaryCard(
    summary: ServiceSummary?,
    onClick: () -> Unit,
    onGoToSettings: () -> Unit,
) {
    val service = summary?.service ?: return
    AppCard(modifier = Modifier.fillMaxWidth(), onClick = if (summary is ServiceSummary.Loaded) onClick else null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor(summary), CircleShape),
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(service.displayName, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 2.dp))
                when (summary) {
                    is ServiceSummary.NotConfigured ->
                        Text("Not connected", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    is ServiceSummary.Loading ->
                        Text("Loading…", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    is ServiceSummary.Error ->
                        Text(summary.message, color = StatusError, style = MaterialTheme.typography.bodySmall)
                    is ServiceSummary.Loaded -> {
                        Text(summary.primaryLabel, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        if (summary.secondaryLabel.isNotBlank()) {
                            Text(summary.secondaryLabel, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            when (summary) {
                is ServiceSummary.Loading -> CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AccentGreen)
                is ServiceSummary.NotConfigured -> PillButton(text = "Connect", onClick = onGoToSettings, verticalPadding = 8.dp)
                else -> {}
            }
        }
    }
}

private fun dotColor(summary: ServiceSummary) = when (summary) {
    is ServiceSummary.Loaded -> AccentGreen
    is ServiceSummary.Error -> StatusError
    else -> TextMuted
}
