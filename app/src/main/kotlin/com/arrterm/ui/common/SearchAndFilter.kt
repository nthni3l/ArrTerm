package com.arrterm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.CardSurface
import com.arrterm.ui.theme.OnAccent
import com.arrterm.ui.theme.PillShape
import com.arrterm.ui.theme.Sora
import com.arrterm.ui.theme.TextMuted
import com.arrterm.ui.theme.TextPrimary
import com.arrterm.ui.theme.TextSecondary

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontFamily = Sora),
        cursorBrush = SolidColor(AccentGreen),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        decorationBox = { innerField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                    innerField()
                }
                if (value.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .clickable { onValueChange("") }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("×", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
    )
}

@Composable
fun FilterChipRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .let {
                        if (selected) it.background(AccentGreen)
                        else it.border(1.dp, Color.White.copy(alpha = 0.2f), PillShape)
                    }
                    .clickable { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = label,
                    color = if (selected) OnAccent else TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = Sora,
                )
            }
        }
    }
}
