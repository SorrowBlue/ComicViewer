package com.sorrowblue.comicviewer.file.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun Modifier.rotatingBorder(
    isVisible: Boolean = true,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 4.dp,
    borderColor: Color = Color.Blue,
    segmentLengthRatio: Float = 0.25f,
    durationMillis: Int = 2000,
    strokeCap: StrokeCap = StrokeCap.Butt,
): Modifier = this then RotatingBorderElement(
    isVisible,
    shape,
    borderWidth,
    borderColor,
    segmentLengthRatio,
    durationMillis,
    strokeCap,
)

private data class RotatingBorderElement(
    val isVisible: Boolean,
    val shape: Shape,
    val borderWidth: Dp,
    val borderColor: Color,
    val segmentLengthRatio: Float,
    val durationMillis: Int,
    val strokeCap: StrokeCap,
) : ModifierNodeElement<RotatingBorderNode>() {
    override fun create(): RotatingBorderNode = RotatingBorderNode(
        isVisible,
        shape,
        borderWidth,
        borderColor,
        segmentLengthRatio,
        durationMillis,
        strokeCap,
    )

    override fun update(node: RotatingBorderNode) {
        val oldVisible = node.isVisible
        node.isVisible = isVisible
        node.shape = shape
        node.borderWidth = borderWidth
        node.borderColor = borderColor
        node.segmentLengthRatio = segmentLengthRatio
        node.durationMillis = durationMillis
        node.strokeCap = strokeCap

        if (oldVisible != isVisible || isVisible) {
            node.updateAnimation()
        }
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "rotatingBorder"
        properties["isVisible"] = isVisible
        properties["shape"] = shape
        properties["borderWidth"] = borderWidth
    }
}

private class RotatingBorderNode(
    var isVisible: Boolean,
    var shape: Shape,
    var borderWidth: Dp,
    var borderColor: Color,
    var segmentLengthRatio: Float,
    var durationMillis: Int,
    var strokeCap: StrokeCap,
) : Modifier.Node(),
    DrawModifierNode {

    private val progress = Animatable(0f)
    private var animationJob: Job? = null

    private val pathMeasure = PathMeasure()
    private val basePath = Path()
    private val segmentPath = Path()
    private var lastSize: Size = Size.Unspecified

    override fun onAttach() {
        updateAnimation()
    }

    fun updateAnimation() {
        animationJob?.cancel()
        if (isVisible) {
            animationJob = coroutineScope.launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                )
            }
        } else {
            coroutineScope.launch { progress.snapTo(0f) }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        if (!isVisible) return

        val strokeWidthPx = borderWidth.toPx()

        if (lastSize != size) {
            lastSize = size
            updateBasePath(strokeWidthPx)
        }

        pathMeasure.setPath(basePath, false)
        val pathLength = pathMeasure.length
        val segmentLength = pathLength * segmentLengthRatio.coerceIn(0f, 1f)

        val currentProgress = progress.value
        val startDistance = (currentProgress * pathLength) % pathLength
        val endDistance = (startDistance + segmentLength) % pathLength

        segmentPath.reset()
        if (startDistance < endDistance) {
            pathMeasure.getSegment(startDistance, endDistance, segmentPath, true)
        } else {
            pathMeasure.getSegment(startDistance, pathLength, segmentPath, true)
            pathMeasure.getSegment(0f, endDistance, segmentPath, true)
        }

        val offset = strokeWidthPx / 2f
        translate(left = offset, top = offset) {
            drawPath(
                path = segmentPath,
                color = borderColor,
                style = Stroke(
                    width = strokeWidthPx,
                    cap = strokeCap,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }

    private fun ContentDrawScope.updateBasePath(strokeWidthPx: Float) {
        basePath.reset()
        val insetSize = Size(
            width = (size.width - strokeWidthPx).coerceAtLeast(0f),
            height = (size.height - strokeWidthPx).coerceAtLeast(0f),
        )
        val outline = shape.createOutline(insetSize, layoutDirection, this)
        basePath.addOutline(outline)
    }
}
