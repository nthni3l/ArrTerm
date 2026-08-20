package com.arrterm.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arrterm.ui.theme.CloudWhite
import com.arrterm.ui.theme.SkyBlueDeep
import com.arrterm.ui.theme.SkyBluePale

/**
 * A translucent "crystal bubble" surface: soft tinted shadow, glossy diagonal
 * gradient fill, and a pale rim-light border. Used for every interactive
 * surface (buttons, badges, cards, nav items) to give the app its glassy look.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = SkyBlueDeep,
    fillAlphaTop: Float = 0.95f,
    fillAlphaBottom: Float = 0.35f,
    elevation: Dp = 10.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = tint.copy(alpha = 0.25f),
                spotColor = tint.copy(alpha = 0.35f),
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        CloudWhite.copy(alpha = fillAlphaTop),
                        tint.copy(alpha = fillAlphaBottom * 0.5f),
                        tint.copy(alpha = fillAlphaBottom),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 300f),
                ),
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(CloudWhite, SkyBluePale.copy(alpha = 0.6f))),
                ),
                shape,
            ),
    ) {
        content()
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = SkyBlueDeep,
    enabled: Boolean = true,
) {
    GlassSurface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = CircleShape,
        tint = tint,
        elevation = 6.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (enabled) tint else tint.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    tint: Color = SkyBlueDeep,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }
    GlassSurface(
        modifier = modifier.then(clickModifier),
        shape = RoundedCornerShape(24.dp),
        tint = tint,
        fillAlphaTop = 0.9f,
        fillAlphaBottom = 0.18f,
        elevation = 8.dp,
    ) {
        content()
    }
}

