package com.digitalspeedometer.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Lets the user pick the speedometer's number colour and background colour,
 * each via a full HSV colour-gradient picker (see GradientColorPicker) rather
 * than a fixed set of swatches.
 */
@Composable
fun ColorPickerDialog(
    currentNumberColor: Color,
    currentBackgroundColor: Color,
    onNumberColorChange: (Color) -> Unit,
    onBackgroundColorChange: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        },
        title = { Text("Speedometer colours") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TabRow(selectedTabIndex = tabIndex, modifier = Modifier.fillMaxWidth()) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Numbers") })
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Background") })
                }

                Column(modifier = Modifier.padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    // key() via if/else creates a fresh GradientColorPicker instance per tab,
                    // so each one seeds its own HSV state from the right starting colour.
                    if (tabIndex == 0) {
                        GradientColorPicker(color = currentNumberColor, onColorChange = onNumberColorChange)
                    } else {
                        GradientColorPicker(color = currentBackgroundColor, onColorChange = onBackgroundColorChange)
                    }
                }
            }
        }
    )
}
