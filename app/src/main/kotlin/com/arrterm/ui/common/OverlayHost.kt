package com.arrterm.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.CardSurface
import com.arrterm.ui.theme.DeleteScrim
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.TextBody
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextSecondary
import com.arrterm.ui.theme.ToastBackground
import com.arrterm.ui.theme.ToastText
import kotlinx.coroutines.delay

/** Hosts the app-wide toast and delete-confirmation overlays above everything else, including the bottom nav. */
@Composable
fun AppOverlayHost() {
    ToastOverlay()
    DeleteDialogOverlay()
}

@Composable
private fun ToastOverlay() {
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ToastBus.events.collect {
            message = it
        }
    }
    LaunchedEffect(message) {
        if (message != null) {
            delay(1800)
            message = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = message != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 92.dp),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(ToastBackground)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)), RoundedCornerShape(100))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            ) {
                Text(text = message.orEmpty(), color = ToastText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DeleteDialogOverlay() {
    val request by DeleteDialogController.request.collectAsState()
    val current = request ?: return
    var deleteFiles by remember(current) { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeleteScrim)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Remove this title?",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { deleteFiles = !deleteFiles }
                        .padding(bottom = 20.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (deleteFiles) AccentGreen else Color.Transparent)
                            .border(1.5.dp, TextSecondary, RoundedCornerShape(4.dp)),
                    )
                    Text(text = "Also delete the files from disk", color = TextBody, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PillButton(
                        text = "Cancel",
                        onClick = { DeleteDialogController.dismiss() },
                        filled = false,
                        verticalPadding = 11.dp,
                        modifier = Modifier.weight(1f),
                    )
                    PillButton(
                        text = "Delete",
                        onClick = {
                            val confirm = current.onConfirm
                            DeleteDialogController.dismiss()
                            confirm(deleteFiles)
                        },
                        tint = StatusError,
                        verticalPadding = 11.dp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
