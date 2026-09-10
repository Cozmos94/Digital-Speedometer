package com.digitalspeedometer.app.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/** Shown user-facing text — this is the version that actually matters legally, unlike a README note. */
const val ACCURACY_DISCLAIMER_TEXT = "Digital Speedometer estimates your speed using your device's GPS. " +
    "GPS-based speed readings can be delayed, temporarily inaccurate, or briefly unavailable " +
    "(for example in tunnels, near tall buildings, or with poor signal), and may not exactly " +
    "match your vehicle's built-in speedometer. This app is provided for general reference only " +
    "— always rely on your vehicle's own speedometer to comply with posted speed limits and " +
    "traffic laws. Do not rely solely on this app while driving."

/**
 * A one-time, non-dismissable (no tap-outside/back-button escape) disclaimer shown before the
 * user can start the speedometer. [onAcknowledge] should persist the acknowledgement
 * (PreferencesManager.hasAcknowledgedDisclaimer) so this doesn't show again.
 */
@Composable
fun DisclaimerDialog(onAcknowledge: () -> Unit) {
    AlertDialog(
        onDismissRequest = {}, // must be explicitly acknowledged — no tap-outside/back dismissal
        title = { Text("Before you start") },
        text = { Text(ACCURACY_DISCLAIMER_TEXT) },
        confirmButton = {
            TextButton(onClick = onAcknowledge) { Text("I Understand") }
        },
    )
}
