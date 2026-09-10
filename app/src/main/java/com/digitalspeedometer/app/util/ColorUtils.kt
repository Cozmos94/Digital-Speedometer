package com.digitalspeedometer.app.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Packs this Color into a standard 32-bit ARGB Int — for SharedPreferences
 * storage and for handing off to android.graphics.Color's HSV utilities,
 * neither of which understand Compose's own Color type.
 */
fun Color.toArgbInt(): Int {
    val a = (alpha * 255f).roundToInt().coerceIn(0, 255)
    val r = (red * 255f).roundToInt().coerceIn(0, 255)
    val g = (green * 255f).roundToInt().coerceIn(0, 255)
    val b = (blue * 255f).roundToInt().coerceIn(0, 255)
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}
