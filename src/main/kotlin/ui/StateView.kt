package ui

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import data.State
import data.TypeState
import org.jetbrains.skia.*

data class StateCoords(val offset: MutableState<Offset>, val id: State)

val PrimaryBlue = Color(0xFF3F51B5)
val SecondaryTeal = Color(0xFF009688)
val AccentAmber = Color(0xFFFFC107)
val FinalRed = Color(0xFFE91E63)

fun DrawScope.drawState(state: StateCoords, num: Int) {
    val radius = 30f
    val center = state.offset.value

    when (state.id.type.value) {
        TypeState.NORMAL -> {
            drawCircle(color = PrimaryBlue, radius = radius, center = center)
            drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(width = 3f))
        }
        TypeState.FINAL -> {
            drawCircle(color = FinalRed, radius = radius, center = center)
            drawCircle(color = Color.White, radius = radius - 5f, center = center, style = Stroke(width = 2f))
            drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(width = 3f))
        }
        TypeState.INITIAL -> {
            drawCircle(color = SecondaryTeal, radius = radius, center = center)
            drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(width = 3f))
            
            // Initial state arrow
            drawPath(
                path = Path().apply {
                    moveTo(center.x - radius - 5f, center.y)
                    lineTo(center.x - radius - 25f, center.y + 15f)
                    lineTo(center.x - radius - 25f, center.y - 15f)
                    close()
                },
                color = SecondaryTeal,
            )
        }
    }
    
    drawIntoCanvas { canvas ->
        val text = num.toString()
        val paint = org.jetbrains.skia.Paint().apply { 
            color = Color.White.toArgb()
            isAntiAlias = true
        }
        val font = Font(Typeface.makeFromName("Segoe UI", FontStyle.BOLD), 18f)
        val textLine = TextLine.make(text, font)
        
        // Center text
        val x = center.x - (textLine.width / 2)
        val y = center.y + (font.metrics.capHeight / 2)
        
        canvas.nativeCanvas.drawTextLine(textLine, x, y, paint)
    }
}