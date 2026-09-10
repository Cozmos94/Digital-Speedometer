package com.digitalspeedometer.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.digitalspeedometer.app.util.toArgbInt
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import android.graphics.Color as AndroidColor

private val WheelDiameter = 220.dp
private val RingThickness = 26.dp
private val RingToCircleGap = 8.dp

/**
 * A colour wheel drawn as a single Canvas (ring + centre circle drawn together,
 * so there's no risk of one overlapping/clipping the other): hue chosen around
 * the outer ring, saturation/value chosen inside the centre circle — white in
 * the middle fading out to the fully saturated hue, darkening to black towards
 * the bottom. Built on Canvas + android.graphics.Color's HSV helpers, no
 * third-party colour-picker dependency.
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

    fun handleTouch(touch: Offset, canvasSizePx: Float, ringWidthPx: Float, gapPx: Float) {
        val c = canvasSizePx / 2f
        val innerRadius = c - ringWidthPx - gapPx
        val dx = touch.x - c
        val dy = touch.y - c
        if (hypot(dx, dy) <= innerRadius) {
            saturation = ((touch.x - (c - innerRadius)) / (innerRadius * 2f)).coerceIn(0f, 1f)
            value = 1f - ((touch.y - (c - innerRadius)) / (innerRadius * 2f)).coerceIn(0f, 1f)
        } else {
            val degrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
            hue = if (degrees < 0f) degrees + 360f else degrees
        }
        emitColor()
    }

    val hueColor = remember(hue) { Color(AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))) }
    val hueRingColors = remember {
        (0..360 step 10).map { h -> Color(AndroidColor.HSVToColor(floatArrayOf(h.toFloat(), 1f, 1f))) }
    }

    Canvas(
        modifier = modifier
            .size(WheelDiameter)
            .pointerInput(Unit) {
                val (ringPx, gapPx) = ringAndGapPx()
                detectTapGestures { offset -> handleTouch(offset, size.width.toFloat(), ringPx, gapPx) }
            }
            .pointerInput(Unit) {
                val (ringPx, gapPx) = ringAndGapPx()
                detectDragGestures { change, _ -> handleTouch(change.position, size.width.toFloat(), ringPx, gapPx) }
            }
    ) {
        val outerRadius = size.minDimension / 2f
        val ringWidthPx = RingThickness.toPx()
        val gapPx = RingToCircleGap.toPx()
        val innerRadius = outerRadius - ringWidthPx - gapPx

        // Outer hue ring.
        drawCircle(
            brush = Brush.sweepGradient(hueRingColors),
            radius = outerRadius - ringWidthPx / 2f,
            style = Stroke(width = ringWidthPx),
        )
        val hueAngleRad = Math.toRadians(hue.toDouble())
        val hueMarker = Offset(
            x = center.x + (outerRadius - ringWidthPx / 2f) * cos(hueAngleRad).toFloat(),
            y = center.y + (outerRadius - ringWidthPx / 2f) * sin(hueAngleRad).toFloat(),
        )
        drawCircle(color = Color.White, radius = ringWidthPx / 2f - 3f, center = hueMarker, style = Stroke(width = 4f))

        // Centre saturation (white -> hue, left to right) / value (transparent -> black, top to bottom) circle.
        val innerTopLeft = Offset(center.x - innerRadius, center.y - innerRadius)
        val innerSize = Size(innerRadius * 2f, innerRadius * 2f)
        val ovalPath = Path().apply { addOval(Rect(innerTopLeft, innerSize)) }

        clipPath(ovalPath) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, hueColor),
                    startX = innerTopLeft.x,
                    endX = innerTopLeft.x + innerSize.width,
                ),
                topLeft = innerTopLeft,
                size = innerSize,
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = innerTopLeft.y,
                    endY = innerTopLeft.y + innerSize.height,
                ),
                topLeft = innerTopLeft,
                size = innerSize,
            )
        }

        val svMarker = Offset(
            innerTopLeft.x + saturation * innerSize.width,
            innerTopLeft.y + (1f - value) * innerSize.height,
        )
        drawCircle(color = Color.Black, radius = 11f, center = svMarker, style = Stroke(width = 2f))
        drawCircle(color = Color.White, radius = 8f, center = svMarker, style = Stroke(width = 2f))
    }
}

/** RingThickness/RingToCircleGap converted to px — usable from a PointerInputScope (also a Density). */
private fun PointerInputScope.ringAndGapPx(): Pair<Float, Float> =
    RingThickness.toPx() to RingToCircleGap.toPx()
