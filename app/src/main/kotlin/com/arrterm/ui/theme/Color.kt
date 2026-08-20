package com.arrterm.ui.theme

import androidx.compose.ui.graphics.Color

// Dark palette from the imported "ArrTerm Mobile" design
val AppBackground = Color(0xFF17191A)
val CardSurface = Color(0xFF1E2123)
val CardBorder = Color(0x14FFFFFF) // white @ 8%

val TextPrimary = Color(0xFFEDEFEF)
val TextBody = Color(0xFFC7CDCB)
val TextSecondary = Color(0xFF9AA1A0)
val TextMuted = Color(0xFF6E7574)

val AccentGreen = Color(0xFF2E9C6F)
val ApproveGreen = Color(0xFF6EBE8F)
val StatusSuccess = Color(0xFF4FBE8C)
val StatusWarning = Color(0xFFE0A458)
// The design defines an unused C.error (#E2665A) but every actual delete/decline
// element (buttons, borders, dialog) hardcodes this slightly different red instead.
val StatusError = Color(0xFFE0645A)

val OnAccent = Color(0xFF17140F)
val ToastBackground = Color(0xFFEDEFEF)
val ToastText = Color(0xFF17191A)

val PosterStripeDark = Color(0xFF202426)
val PosterStripeLight = Color(0xFF2A2F31)

val DeleteScrim = Color(0x73FFFFFF) // white @ 45%, matches the design's inverted scrim
