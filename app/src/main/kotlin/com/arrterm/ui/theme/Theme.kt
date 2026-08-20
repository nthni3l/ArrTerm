package com.arrterm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ArrDarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = OnAccent,
    secondary = ApproveGreen,
    onSecondary = OnAccent,
    tertiary = StatusWarning,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = StatusError,
    onError = TextPrimary,
)

val PillShape = RoundedCornerShape(100)

private val ArrShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = PillShape,
)

@Composable
fun ArrTermTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArrDarkColorScheme,
        typography = TermTypography,
        shapes = ArrShapes,
        content = content,
    )
}
