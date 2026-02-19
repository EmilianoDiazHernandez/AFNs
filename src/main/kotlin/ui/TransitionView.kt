package ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import data.Transition
import org.jetbrains.skia.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class TransitionCoords (val coordState1: StateCoords, val coordState2: StateCoords, val id: Transition)

fun DrawScope.drawArrow(from: Offset, to: Offset, chars: List<Char>, color: Color) {
    val strokeWidth = 2.5f
    val arrowSize = 12f
    val stateRadius = 30f
    val labelFont = Font(Typeface.makeFromName("Segoe UI", FontStyle.BOLD_ITALIC), 14f)
    val labelPaint = org.jetbrains.skia.Paint().apply {
        this.color = Color(0xFF333333).toArgb()
        isAntiAlias = true
    }
    val labelBgPaint = org.jetbrains.skia.Paint().apply {
        this.color = Color.White.copy(alpha = 0.8f).toArgb()
    }

    if (from == to) {
        val loopRadius = 25f
        val center = Offset(from.x, from.y - stateRadius - loopRadius + 5f)

        drawArc(
            color = color,
            startAngle = 40f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(center.x - loopRadius, center.y - loopRadius),
            size = Size(loopRadius * 2, loopRadius * 2),
            style = Stroke(width = strokeWidth)
        )

        // Arrowhead for loop
        val arrowAngle = Math.toRadians(40.0).toFloat()
        val arrowTip = Offset(
            center.x + loopRadius * cos(arrowAngle),
            center.y + loopRadius * sin(arrowAngle)
        )
        
        drawPath(
            path = Path().apply {
                moveTo(arrowTip.x, arrowTip.y)
                lineTo(arrowTip.x + 5f, arrowTip.y + 12f)
                lineTo(arrowTip.x - 8f, arrowTip.y + 5f)
                close()
            },
            color = color,
        )

        // Text positioning
        val text = chars.joinToString(",")
        val textLine = TextLine.make(text, labelFont)
        val textPos = Offset(center.x - textLine.width / 2, center.y - loopRadius - 5f)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRect(
                org.jetbrains.skia.Rect.makeXYWH(textPos.x - 2f, textPos.y - textLine.height, textLine.width + 4f, textLine.height + 2f),
                labelBgPaint
            )
            canvas.nativeCanvas.drawTextLine(textLine, textPos.x, textPos.y, labelPaint)
        }

    } else {
        val direction = Offset(to.x - from.x, to.y - from.y)
        val length = direction.getDistance()

        val unitDirection = if (length > 0) Offset(direction.x / length, direction.y / length) else Offset(0f, 0f)
        val startPoint = Offset(from.x + unitDirection.x * stateRadius, from.y + unitDirection.y * stateRadius)
        val endPoint = Offset(to.x - unitDirection.x * stateRadius, to.y - unitDirection.y * stateRadius)

        drawLine(color = color, start = startPoint, end = endPoint, strokeWidth = strokeWidth)

        val angle = atan2(endPoint.y - from.y, endPoint.x - from.x)
        val arrowPoint1 = Offset(
            x = endPoint.x - (arrowSize * cos(angle - Math.PI / 6)).toFloat(),
            y = endPoint.y - (arrowSize * sin(angle - Math.PI / 6)).toFloat()
        )
        val arrowPoint2 = Offset(
            x = endPoint.x - (arrowSize * cos(angle + Math.PI / 6)).toFloat(),
            y = endPoint.y - (arrowSize * sin(angle + Math.PI / 6)).toFloat()
        )

        val path = Path().apply {
            moveTo(endPoint.x, endPoint.y)
            lineTo(arrowPoint1.x, arrowPoint1.y)
            lineTo(arrowPoint2.x, arrowPoint2.y)
            close()
        }
        drawPath(path = path, color = color)

        // Labels
        val midPoint = Offset((from.x + to.x) / 2, (from.y + to.y) / 2)
        val perpendicularOffset = Offset(unitDirection.y, -unitDirection.x) * 18f
        val text = chars.joinToString(",")
        val textLine = TextLine.make(text, labelFont)
        val textPos = midPoint + perpendicularOffset - Offset(textLine.width / 2, -textLine.height / 2)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawRect(
                org.jetbrains.skia.Rect.makeXYWH(textPos.x - 2f, textPos.y - textLine.height, textLine.width + 4f, textLine.height + 2f),
                labelBgPaint
            )
            canvas.nativeCanvas.drawTextLine(textLine, textPos.x, textPos.y, labelPaint)
        }
    }
}