package com.digitalspeedometer.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.digitalspeedometer.app.util.toArgbInt
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Color as AndroidColor

private val WheelDiameter = 220.dp
private val RingThickness = 26.dp
private val RingToCircleGap = 8.dp
private val InnerCircleDiameter: Dp = WheelDiameter - (RingThickness * 2) - (RingToCircleGap * 2)

/**
 * A colour wheel: hue chosen around the outer ring, saturation/value chosen
 * inside the centre circle (white in the middle fading out to the fully
 * saturated hue, darkening to black towards the bottom) — same idea as the
 * classic circular colour pickers. Built on Canvas + android.graphics.Color's
 * HSV helpers, no third-party colour-picker library.
 */
@Composable
fun GradientColorPicker(color: Color, onColorChange: (Color) -> Unit, modifier: Modifier = Modifier) {
    // Seed local HSV state once from the incoming colour; after that this
    // composable's own state drives everything (re-deriving HSV from the
    // colour every recomposition would lose the hue whenever the user drags
    // into a fully desaturated or black/white spot, since hue is undefined there).
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
    val hueRingColors = remember {
        (0..360 step 10).map { h -> Color(AndroidColor.HSVToColor(floatArrayOf(h.toFloat(), 1f, 1f))) }
    }

    Box(modifier = modifier.size(WheelDiameter), contentAlignment = Alignment.Center) {
        // Outer hue ring.
        Canvas(
            modifier = Modifier
                .size(WheelDiameter)
                .pointerInput(Unit) {
                    detectTapGestures { offset -> hue = angleOfTouch(offset, this.size); emitColor() }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ -> hue = angleOfTouch(change.position, this.size); emitColor() }
                }
        ) {
            val ringWidthPx = RingThickness.toPx()
            val ringRadius = (size.minDimension - ringWidthPx) / 2f
            drawCircle(
                brush = Brush.sweepGradient(hueRingColors),
                radius = ringRadius,
                style = Stroke(width = ringWidthPx),
            )

            val angleRad = Math.toRadians(hue.toDouble())
            val marker = Offset(
                x = center.x + ringRadius * cos(angleRad).toFloat(),
                y = center.y + ringRadius * sin(angleRad).toFloat(),
            )
            drawCircle(color = Color.White, radius = ringWidthPx / 2f - 3f, center = marker, style = Stroke(width = 4f))
        }

        // Inner saturation (white -> hue, left to right) / value (transparent -> black, top to bottom) circle.
        Canvas(
            modifier = Modifier
                .size(InnerCircleDiameter)
                .clip(CircleShape)
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
    }
}

/** Angle (0-360°) of [touch] around the centre of a square canvas of [canvasSize], matching Brush.sweepGradient's convention. */
private fun angleOfTouch(touch: Offset, canvasSize: androidx.compose.ui.unit.IntSize): Float {
    val centerX = canvasSize.width / 2f
    val centerY = canvasSize.height / 2f
    val degrees = Math.toDegrees(atan2((touch.y - centerY).toDouble(), (touch.x - centerX).toDouble())).toFloat()
    return if (degrees < 0f) degrees + 360f else degrees
}
