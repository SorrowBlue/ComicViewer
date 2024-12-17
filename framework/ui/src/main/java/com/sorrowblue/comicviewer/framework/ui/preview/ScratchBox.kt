package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scratch(color: Color, strokeWidth: Dp = 1.dp): Modifier {
    val density = LocalDensity.current
    return drawWithContent {
        drawContent()
        inset {
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(size.width, 0f),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(0f, size.height),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawRect(color, Offset(0f, 0f), Size(16.dp.toPx(), 16.dp.toPx()))
            drawRect(
                color,
                Offset(0f, size.height - 16.dp.toPx()),
                Size(16.dp.toPx(), 16.dp.toPx())
            )
            drawRect(
                color,
                Offset(size.width - 16.dp.toPx(), 0f),
                Size(16.dp.toPx(), 16.dp.toPx()),
            )
            drawRect(
                color,
                Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                Size(16.dp.toPx(), 16.dp.toPx()),
            )
            val max = Math.round(size.width / 100)
            with(density) {
                for (i in 0..max) {
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(size.width, size.height / max * (max - i)),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = color.alpha
                    )
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(0f, size.height / max * i),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = color.alpha
                    )
                    if (i > 0) {
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(0f, size.height / max * i),
                            Offset(size.width / max * (max - i), size.height),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = color.alpha
                        )
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(size.width / max * i, size.height),
                            Offset(size.width, (size.height / max) * i),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = color.alpha
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScratchBox(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        inset {
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(size.width, 0f),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawLine(
                color,
                Offset(0f, size.height),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx(),
                alpha = color.alpha
            )
            drawRect(color, Offset(0f, 0f), Size(16.dp.toPx(), 16.dp.toPx()))
            drawRect(
                color,
                Offset(0f, size.height - 16.dp.toPx()),
                Size(16.dp.toPx(), 16.dp.toPx())
            )
            drawRect(
                color,
                Offset(size.width - 16.dp.toPx(), 0f),
                Size(16.dp.toPx(), 16.dp.toPx()),
            )
            drawRect(
                color,
                Offset(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                Size(16.dp.toPx(), 16.dp.toPx()),
            )
            val max = Math.round(size.width / 100)
            with(density) {
                for (i in 0..max) {
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(size.width, size.height / max * (max - i)),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = color.alpha
                    )
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(0f, size.height / max * i),
                        strokeWidth = strokeWidth.toPx(),
                        alpha = color.alpha
                    )
                    if (i > 0) {
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(0f, size.height / max * i),
                            Offset(size.width / max * (max - i), size.height),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = color.alpha
                        )
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(size.width / max * i, size.height),
                            Offset(size.width, (size.height / max) * i),
                            strokeWidth = strokeWidth.toPx(),
                            alpha = color.alpha
                        )
                    }
                }
            }
        }
    }
}
