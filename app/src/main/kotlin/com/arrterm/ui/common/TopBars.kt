package com.arrterm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arrterm.ui.theme.CardBorder
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary

@Composable
private fun BottomDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(1.dp).background(CardBorder))
}

@Composable
fun TabTopBar(title: String, count: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 14.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(text = title, color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            if (count.isNotBlank()) {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 4.dp))
                Text(text = count, color = TextMuted, fontFamily = JetBrainsMono, style = MaterialTheme.typography.bodySmall)
            }
        }
        BottomDivider()
    }
}

@Composable
fun DetailTopBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.06f), CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "‹", color = TextPrimary, fontSize = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp))
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 10.dp))
            Text(
                text = title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BottomDivider()
    }
}
