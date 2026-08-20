package com.arrterm.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RadarrNavIcon(color: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(14.dp)
            .rotate(45f)
            // 20dp square rotated 45° would visually dominate the row next to the
            // other 20dp icons (its corner-to-corner span is ~28dp); sized down to
            // read as the same visual weight as the circle/pill/ring beside it.
            .background(color),
    )
}

@Composable
fun SonarrNavIcon(color: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(width = 22.dp, height = 16.dp)
            .background(color, RoundedCornerShape(3.dp)),
    )
}

@Composable
fun OverseerrNavIcon(color: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(20.dp)
            .background(color, CircleShape),
    )
}

@Composable
fun SettingsNavIcon(color: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(20.dp)
            .border(4.dp, color, CircleShape),
    )
}
