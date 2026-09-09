package com.digitalspeedometer.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color

/** Speed unit the user wants the readout displayed in. */
enum class SpeedUnit(val label: String, val metersPerSecondToUnit: (Float) -> Float) {
    KMH("KM/H", { mps -> mps * 3.6f }),
    MPH("MPH", { mps -> mps * 2.23694f });

    fun toggled(): SpeedUnit = if (this == KMH) MPH else KMH
}

/**
 * Thin wrapper around SharedPreferences that persists the user's speedometer
 * settings (mirror state, unit, and the two custom colours) between launches.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isMirrored: Boolean
        get() = prefs.getBoolean(KEY_MIRRORED, true) // mirrored (windshield reflection) is the default
        set(value) = prefs.edit().putBoolean(KEY_MIRRORED, value).apply()

    var speedUnit: SpeedUnit
        get() = SpeedUnit.valueOf(prefs.getString(KEY_UNIT, SpeedUnit.KMH.name) ?: SpeedUnit.KMH.name)
        set(value) = prefs.edit().putString(KEY_UNIT, value.name).apply()

    var numberColor: Color
        get() = Color(prefs.getInt(KEY_NUMBER_COLOR, DEFAULT_NUMBER_COLOR))
        set(value) = prefs.edit().putInt(KEY_NUMBER_COLOR, value.toArgbInt()).apply()

    var backgroundColor: Color
        get() = Color(prefs.getInt(KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR))
        set(value) = prefs.edit().putInt(KEY_BACKGROUND_COLOR, value.toArgbInt()).apply()

    private fun Color.toArgbInt(): Int {
        val a = (alpha * 255f).toInt() and 0xFF
        val r = (red * 255f).toInt() and 0xFF
        val g = (green * 255f).toInt() and 0xFF
        val b = (blue * 255f).toInt() and 0xFF
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    companion object {
        private const val PREFS_NAME = "digital_speedometer_prefs"
        private const val KEY_MIRRORED = "mirrored"
        private const val KEY_UNIT = "unit"
        private const val KEY_NUMBER_COLOR = "number_color"
        private const val KEY_BACKGROUND_COLOR = "background_color"

        private const val DEFAULT_NUMBER_COLOR = 0xFF00E676.toInt() // bright green, reads well reflected
        private const val DEFAULT_BACKGROUND_COLOR = 0xFF000000.toInt() // black
    }
}
