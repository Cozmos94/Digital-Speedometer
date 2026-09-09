package com.digitalspeedometer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.digitalspeedometer.app.ui.theme.ColorPresets

/**
 * Lets the user pick the speedometer's number colour and background colour
 * from a preset swatch grid. Kept dependency-free (no third-party colour-picker
 * library) so the build stays simple.
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
            Column {
                TabRow(selectedTabIndex = tabIndex) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Numbers") })
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Background") })
                }
                val selectedColor = if (tabIndex == 0) currentNumberColor else currentBackgroundColor
                val onSelect: (Color) -> Unit = if (tabIndex == 0) onNumberColorChange else onBackgroundColorChange

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(ColorPresets) { swatch ->
                        val isSelected = swatch.value == selectedColor.value
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { onSelect(swatch) }
                                .background(swatch, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2979FF) else Color(0xFF9E9E9E),
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {}
                    }
                }
            }
        }
    )
}
