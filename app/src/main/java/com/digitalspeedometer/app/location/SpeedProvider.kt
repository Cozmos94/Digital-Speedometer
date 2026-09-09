package com.digitalspeedometer.app.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/** True if the app currently holds a location permission suitable for GPS speed updates. */
fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

/**
 * Streams the device's current ground speed in metres/second while [active] is true,
 * using the fused location provider's built-in speed reading (GPS Doppler-derived,
 * far steadier than differentiating successive positions ourselves).
 *
 * Safe to call even when permission hasn't been granted yet: it simply reports 0f
 * until [active] flips true (the caller is expected to only pass active = true once
 * hasLocationPermission(context) is true).
 */
@Composable
fun rememberSpeedMetersPerSecond(context: Context, active: Boolean): State<Float> {
    val speed = remember { mutableFloatStateOf(0f) }

    DisposableEffect(active) {
        if (!active || !hasLocationPermission(context)) {
            speed.floatValue = 0f
            return@DisposableEffect onDispose {}
        }

        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                speed.floatValue = if (location.hasSpeed()) location.speed else 0f
            }
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (_: SecurityException) {
            // Permission was revoked between the check above and this call; leave speed at 0.
        }

        onDispose {
            client.removeLocationUpdates(callback)
        }
    }

    return speed
}
