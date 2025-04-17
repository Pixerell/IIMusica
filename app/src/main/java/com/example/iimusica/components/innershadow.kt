package com.example.iimusica.components


import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
): Modifier = this.drawWithContent {
    drawContent()

    drawIntoCanvas { canvas ->
        val paint = Paint()
        paint.color = color

        val spreadPx = spread.toPx()
        val shadowSize = Size(size.width + spreadPx, size.height + spreadPx)
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)
        // Save layer
        canvas.saveLayer(size.toRect(), paint)
        // Draw base shadow
        canvas.drawOutline(shadowOutline, paint)
        // Erase the center
        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0f) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }
        paint.color = Color.Black
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)

        canvas.restore()
    }
}
