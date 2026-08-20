package com.arrterm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arrterm.R

private fun variableWeight(resId: Int, weight: Int) = Font(
    resId = resId,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Sora — headings and body text throughout the design. */
val Sora = FontFamily(
    variableWeight(R.font.sora_variable, 400),
    variableWeight(R.font.sora_variable, 500),
    variableWeight(R.font.sora_variable, 600),
    variableWeight(R.font.sora_variable, 700),
)

/** JetBrains Mono — labels, counts, percentages, and other data-like text. */
val JetBrainsMono = FontFamily(
    variableWeight(R.font.jetbrains_mono_variable, 400),
    variableWeight(R.font.jetbrains_mono_variable, 500),
    variableWeight(R.font.jetbrains_mono_variable, 600),
)

val TermTypography = Typography(
    titleLarge = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Normal, fontSize = 10.sp, letterSpacing = 0.4.sp),
)
