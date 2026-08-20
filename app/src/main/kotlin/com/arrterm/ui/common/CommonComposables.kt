package com.arrterm.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.CardBorder
import com.arrterm.ui.theme.CardSurface
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.OnAccent
import com.arrterm.ui.theme.PillShape
import com.arrterm.ui.theme.PosterStripeDark
import com.arrterm.ui.theme.PosterStripeLight
import com.arrterm.ui.theme.Sora
import com.arrterm.ui.theme.StatusError
import com.arrterm.ui.theme.TextMuted

@Composable
fun AppCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val base = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(CardSurface)
        .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(14.dp))
    Box(modifier = if (onClick != null) modifier.then(base).clickable(onClick = onClick) else modifier.then(base)) {
        content()
    }
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    tint: Color = AccentGreen,
    verticalPadding: Dp = 12.dp,
    fontSize: TextUnit = 14.sp,
    enabled: Boolean = true,
) {
    val outlineColor = if (tint == StatusError) tint.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)
    val shapeMod = Modifier
        .clip(PillShape)
        .let {
            if (filled) it.background(if (enabled) tint else tint.copy(alpha = 0.4f))
            else it.border(BorderStroke(1.dp, outlineColor), PillShape)
        }
        .clickable(enabled = enabled, onClick = onClick)
        .padding(vertical = verticalPadding)
    Box(modifier = modifier.then(shapeMod), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = if (filled) OnAccent else (if (tint == StatusError) tint else MaterialTheme.colorScheme.onSurface),
            fontFamily = Sora,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
        )
    }
}

@Composable
fun StatusBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(PillShape)
            .background(color.copy(alpha = 0.13f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            letterSpacing = 0.4.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(horizontal = 4.dp, vertical = 10.dp),
        color = TextMuted,
        fontFamily = JetBrainsMono,
        fontSize = 11.sp,
        letterSpacing = 0.6.sp,
    )
}

@Composable
fun PosterPlaceholder(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(6.dp), label: String = "IMG") {
    Box(
        modifier = modifier
            .clip(shape)
            .background(PosterStripeDark),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stripe = 12.dp.toPx()
            var x = -size.height
            while (x < size.width) {
                drawLine(
                    color = PosterStripeLight,
                    start = Offset(x, size.height),
                    end = Offset(x + size.height, 0f),
                    strokeWidth = stripe / 2,
                    cap = StrokeCap.Butt,
                )
                x += stripe
            }
        }
        Text(text = label, color = TextMuted, fontFamily = JetBrainsMono, fontSize = 8.sp)
    }
}

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AccentGreen)
    }
}

@Composable
fun FullScreenError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = StatusError, style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))
            PillButton(text = "Retry", onClick = onRetry, filled = false)
        }
    }
}

@Composable
fun NotConfiguredPlaceholder(
    serviceName: String,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "$serviceName isn't connected yet",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Enter a server URL and API key in Settings to connect.",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
            PillButton(text = "Go to Settings", onClick = onGoToSettings)
        }
    }
}
