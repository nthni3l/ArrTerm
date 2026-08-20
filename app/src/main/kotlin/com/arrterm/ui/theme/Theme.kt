package com.arrterm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val TermColorScheme = darkColorScheme(
    primary = TermGreen,
    onPrimary = TermBackground,
    secondary = TermAmber,
    onSecondary = TermBackground,
    tertiary = TermCyan,
    background = TermBackground,
    onBackground = TermTextPrimary,
    surface = TermSurface,
    onSurface = TermTextPrimary,
    surfaceVariant = TermSurfaceVariant,
    onSurfaceVariant = TermTextSecondary,
    outline = TermOutline,
    error = TermRed,
    onError = TermBackground,
)

private val TermShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

@Composable
fun ArrTermTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TermColorScheme,
        typography = TermTypography,
        shapes = TermShapes,
        content = content,
    )
}
