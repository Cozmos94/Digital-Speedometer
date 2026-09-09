package com.digitalspeedometer.app.ui.theme

import androidx.compose.ui.graphics.Color

// App chrome colours (home screen, buttons, dialogs). The speedometer readout
// itself uses user-chosen colours from PreferencesManager, not these.
val SpeedoNavy = Color(0xFF0D1B2A)
val SpeedoNavyLight = Color(0xFF1B2A3D)
val SpeedoAccent = Color(0xFF00E676)
val SpeedoOnDark = Color(0xFFF5F5F5)

/** Preset swatches offered in the colour-picker dialog. */
val ColorPresets = listOf(
    Color(0xFF00E676), // green
    Color(0xFFFFFFFF), // white
    Color(0xFF00E5FF), // cyan
    Color(0xFFFFEA00), // yellow
    Color(0xFFFF6D00), // orange
    Color(0xFFFF1744), // red
    Color(0xFFD500F9), // magenta
    Color(0xFF2979FF), // blue
    Color(0xFF76FF03), // lime
    Color(0xFF000000), // black
    Color(0xFF9E9E9E), // grey
    Color(0xFF212121), // near-black
)
