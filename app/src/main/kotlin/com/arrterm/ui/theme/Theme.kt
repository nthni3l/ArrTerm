package com.arrterm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val SkyColorScheme = lightColorScheme(
    primary = SkyBlueDeep,
    onPrimary = CloudWhite,
    secondary = Aqua,
    onSecondary = InkPrimary,
    tertiary = SkyBluePale,
    background = SkyBackground,
    onBackground = InkPrimary,
    surface = GlassSurface,
    onSurface = InkPrimary,
    surfaceVariant = GlassSurfaceVariant,
    onSurfaceVariant = InkSecondary,
    outline = SkyBluePale,
    error = BubbleError,
    onError = CloudWhite,
)

private val BubbleShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

/** The airy sky-gradient painted behind every screen. */
val SkyGradient: Brush
    @Composable get() = Brush.verticalGradient(listOf(CloudWhite, SkyBackground, SkyBackgroundDeep))

@Composable
fun ArrTermTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SkyColorScheme,
        typography = TermTypography,
        shapes = BubbleShapes,
        content = content,
    )
}
