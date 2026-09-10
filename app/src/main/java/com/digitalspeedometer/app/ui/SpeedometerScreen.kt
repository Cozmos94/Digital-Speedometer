package com.digitalspeedometer.app.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.digitalspeedometer.app.data.PreferencesManager
import com.digitalspeedometer.app.location.hasLocationPermission
import com.digitalspeedometer.app.location.rememberSpeedMetersPerSecond
import kotlin.math.roundToInt

/**
 * The ad-free full-screen speedometer. Mirrored by default so the reflection
 * off a windshield reads correctly; the flip button (top-right) lets someone
 * using the phone screen directly (not the reflection) view it the right way
 * round instead.
 */
@Composable
fun SpeedometerScreen(prefs: PreferencesManager, onBack: () -> Unit) {
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        // Runs once, after the launcher above has actually been registered — calling
        // launch() directly in the composable body instead would crash with
        // "Attempting to launch an unregistered ActivityResultLauncher" on first entry.
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var mirrored by remember { mutableStateOf(prefs.isMirrored) }
    var unit by remember { mutableStateOf(prefs.speedUnit) }
    var numberColor by remember { mutableStateOf(prefs.numberColor) }
    var backgroundColor by remember { mutableStateOf(prefs.backgroundColor) }
    var showColorDialog by remember { mutableStateOf(false) }

    val speedMetersPerSecond by rememberSpeedMetersPerSecond(context, active = hasPermission)
    val displaySpeed = unit.metersPerSecondToUnit(speedMetersPerSecond).roundToInt().coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer { scaleX = if (mirrored) -1f else 1f },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = displaySpeed.toString(),
                color = numberColor,
                fontSize = 140.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = unit.label,
                color = numberColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
            )
            if (!hasPermission) {
                Text(
                    text = "Location permission needed for speed",
                    color = numberColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        FilledIconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0x66000000),
                contentColor = Color.White,
            ),
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to home")
        }

        FilledIconButton(
            onClick = {
                mirrored = !mirrored
                prefs.isMirrored = mirrored
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0x66000000),
                contentColor = Color.White,
            ),
        ) {
            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip mirrored view")
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Units button sits to the left of the colour button, as requested.
            Button(
                onClick = {
                    unit = unit.toggled()
                    prefs.speedUnit = unit
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x66000000),
                    contentColor = Color.White,
                ),
            ) {
                // Shows the unit you'd switch TO, not the current one.
                Text(unit.toggled().label)
            }

            FilledIconButton(
                onClick = { showColorDialog = true },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0x66000000),
                    contentColor = Color.White,
                ),
            ) {
                Icon(Icons.Default.Palette, contentDescription = "Change colours")
            }
        }
    }

    if (showColorDialog) {
        ColorPickerDialog(
            currentNumberColor = numberColor,
            currentBackgroundColor = backgroundColor,
            onNumberColorChange = {
                numberColor = it
                prefs.numberColor = it
            },
            onBackgroundColorChange = {
                backgroundColor = it
                prefs.backgroundColor = it
            },
            onDismiss = { showColorDialog = false },
        )
    }
}
