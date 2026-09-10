package com.digitalspeedometer.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.digitalspeedometer.app.util.toArgbInt
import android.graphics.Color as AndroidColor

/**
 * A full HSV colour-gradient picker: a saturation/value square for the
 * currently selected hue, plus a hue slider underneath. Tap or drag either
 * one. Dependency-free — built on Compose Canvas + android.graphics.Color's
 * HSV conversion helpers rather than a third-party colour-picker library.
 */
@Composable
fun GradientColorPicker(color: Color, onColorChange: (Color) -> Unit, modifier: Modifier = Modifier) {
    // Seed local HSV state once from the incoming colour; after that, this
    // composable's own state drives everything (re-deriving HSV from the
    // colour on every recomposition would lose the hue whenever the user
    // drags into a fully desaturated or black/white spot, since hue is
    // undefined there).
    val initialHsv = remember {
        val out = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgbInt(), out)
        out
    }
    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    fun emitColor() {
        onColorChange(Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, value))))
    }

    val hueColor = remember(hue) { Color(AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Saturation (x) / Value (y) square for the current hue.
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        saturation = (offset.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                        emitColor()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        saturation = (change.position.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                        emitColor()
                    }
                }
        ) {
            drawRect(Brush.horizontalGradient(listOf(Color.White, hueColor)))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))

            val marker = Offset(saturation * size.width, (1f - value) * size.height)
            drawCircle(color = Color.Black, radius = 11f, center = marker, style = Stroke(width = 2f))
            drawCircle(color = Color.White, radius = 8f, center = marker, style = Stroke(width = 2f))
        }

        Spacer(Modifier.height(16.dp))

        // Hue slider.
        val hueGradientColors = remember {
            (0..360 step 15).map { h -> Color(AndroidColor.HSVToColor(floatArrayOf(h.toFloat(), 1f, 1f))) }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        hue = (offset.x / size.width).coerceIn(0f, 1f) * 360f
                        emitColor()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        hue = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                        emitColor()
                    }
                }
        ) {
            drawRect(Brush.horizontalGradient(hueGradientColors))

            val markerX = (hue / 360f) * size.width
            drawCircle(
                color = Color.White,
                radius = size.height / 2f - 2f,
                center = Offset(markerX, size.height / 2f),
                style = Stroke(width = 3f),
            )
        }
    }
}
