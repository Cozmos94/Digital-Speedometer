package com.digitalspeedometer.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.digitalspeedometer.app.util.toArgbInt

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

    /** True once the user has tapped through the accuracy disclaimer at least once. */
    var hasAcknowledgedDisclaimer: Boolean
        get() = prefs.getBoolean(KEY_DISCLAIMER_ACK, false)
        set(value) = prefs.edit().putBoolean(KEY_DISCLAIMER_ACK, value).apply()

    /**
     * Call once each time the user leaves the Speedometer screen. Returns true every
     * [INTERSTITIAL_FREQUENCY_CAP]th call — the signal to show an interstitial ad this time.
     * Persisted, so the count (and cadence) survives app restarts.
     */
    fun registerSpeedometerExitAndShouldShowAd(): Boolean {
        val newCount = prefs.getInt(KEY_EXIT_COUNT, 0) + 1
        prefs.edit().putInt(KEY_EXIT_COUNT, newCount).apply()
        return newCount % INTERSTITIAL_FREQUENCY_CAP == 0
    }

    companion object {
        private const val PREFS_NAME = "digital_speedometer_prefs"
        private const val KEY_MIRRORED = "mirrored"
        private const val KEY_UNIT = "unit"
        private const val KEY_NUMBER_COLOR = "number_color"
        private const val KEY_BACKGROUND_COLOR = "background_color"
        private const val KEY_EXIT_COUNT = "speedometer_exit_count"
        private const val KEY_DISCLAIMER_ACK = "disclaimer_acknowledged"

        private const val DEFAULT_NUMBER_COLOR = 0xFF00E676.toInt() // bright green, reads well reflected
        private const val DEFAULT_BACKGROUND_COLOR = 0xFF000000.toInt() // black

        /** Show an interstitial ad on every 3rd exit from the Speedometer screen, not every time. */
        private const val INTERSTITIAL_FREQUENCY_CAP = 3
    }
}
