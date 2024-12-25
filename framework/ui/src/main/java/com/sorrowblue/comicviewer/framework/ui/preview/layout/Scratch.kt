package com.sorrowblue.comicviewer.framework.ui.preview.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scratch(color: Color, strokeWidth: Dp = 1.dp, alpha: Float = 0.25f): Modifier {
    val density = LocalDensity.current
    return drawWithContent {
        drawContent()
        inset {
            with(density) {
                drawOutline(
                    outline = Outline.Rectangle(Rect(0f, 0f, size.width, size.height)),
                    color = color,
                    alpha = alpha,
                    style = Stroke(strokeWidth.toPx())
                )
            }

            // Draw corner rectangle
            drawRect(
                color = color,
                topLeft = Offset(0f, 0f),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                alpha = alpha
            )
            drawRect(
                color = color,
                topLeft = Offset(0f, size.height - 16.dp.toPx()),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                alpha = alpha
            )
            drawRect(
                color = color,
                topLeft = Offset(size.width - 16.dp.toPx(), 0f),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                alpha = alpha
            )
            drawRect(
                color = color,
                topLeft = Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                alpha = alpha
            )

            // Draw diagonal line
            val max = Math.round(size.width / 100)
            with(density) {
                for (i in 0..max) {
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(size.width, size.height / max * (max - i)),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = alpha
                    )
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(0f, size.height / max * i),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = alpha
                    )
                    if (i > 0) {
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(0f, size.height / max * i),
                            Offset(size.width / max * (max - i), size.height),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = alpha
                        )
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(size.width / max * i, size.height),
                            Offset(size.width, (size.height / max) * i),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = alpha
                        )
                    }
                }
            }
        }
    }
}
