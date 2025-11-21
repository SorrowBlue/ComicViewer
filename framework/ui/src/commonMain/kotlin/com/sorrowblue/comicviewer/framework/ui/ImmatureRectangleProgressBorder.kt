package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val ROTATION_ANIMATION_DURATION = 4000
private const val SWEEP_ANIMATION_DURATION = 4000

fun Modifier.immatureRectangleProgressBorder(
    strokeWidth: Dp = 5.dp,
    cornerRadius: Dp = 12.dp,
    color: Color = Color.Blue,
    enable: Boolean = false,
): Modifier = this then ImmatureBorderNodeElement(
    strokeWidth = strokeWidth,
    cornerRadius = cornerRadius,
    color = color,
    enable = enable,
)

private data class ImmatureBorderNodeElement(
    val strokeWidth: Dp,
    val cornerRadius: Dp,
    val color: Color,
    val enable: Boolean,
) : ModifierNodeElement<ImmatureBorderNode>() {
    override fun create(): ImmatureBorderNode = ImmatureBorderNode(
        strokeWidth = strokeWidth,
        cornerRadius = cornerRadius,
        color = color,
        enable = enable,
    )

    override fun update(node: ImmatureBorderNode) {
        node.update(strokeWidth, cornerRadius, color, enable)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "immatureRectangleProgressBorder"
        properties["strokeWidth"] = strokeWidth
        properties["cornerRadius"] = cornerRadius
        properties["color"] = color
        properties["enable"] = enable
    }
}

private class ImmatureBorderNode(
    var strokeWidth: Dp,
    var cornerRadius: Dp,
    var color: Color,
    var enable: Boolean,
) : Modifier.Node(),
    DrawModifierNode {
    private val visibilityAnim = Animatable(if (enable) 1f else 0f)

    private val rotationAnim = Animatable(0f)

    private val sweepAnim = Animatable(0.1f)

    private val pathMeasure = PathMeasure()
    private val path = Path()
    private val progressPath = Path()

    override fun onAttach() {
        super.onAttach()
        startLoops()
    }

    private fun startLoops() {
        coroutineScope.launch {
            while (true) {
                rotationAnim.snapTo(0f)
                rotationAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(ROTATION_ANIMATION_DURATION, easing = LinearEasing),
                ) {
                    invalidateDraw()
                }
            }
        }

        coroutineScope.launch {
            while (true) {
                sweepAnim.animateTo(
                    targetValue = 0.7f,
                    animationSpec = tween(SWEEP_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                ) { invalidateDraw() }

                sweepAnim.animateTo(
                    targetValue = 0.1f,
                    animationSpec = tween(SWEEP_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                ) { invalidateDraw() }
            }
        }
    }

    fun update(strokeWidth: Dp, cornerRadius: Dp, color: Color, enable: Boolean) {
        if (this.enable != enable) {
            coroutineScope.launch {
                visibilityAnim.animateTo(
                    targetValue = if (enable) 1.0f else 0.0f,
                    animationSpec = tween(durationMillis = 500),
                ) { invalidateDraw() }
            }
        }

        this.strokeWidth = strokeWidth
        this.cornerRadius = cornerRadius
        this.color = color
        this.enable = enable

        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val visibilityFactor = visibilityAnim.value

        if (visibilityFactor == 0f && !enable) {
            drawContent()
            return
        }

        val strokeWidthPx = strokeWidth.toPx()
        val cornerRadiusPx = cornerRadius.toPx()

        path.reset()
        val halfStroke = strokeWidthPx / 2
        path.addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(halfStroke, halfStroke),
                    size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
            ),
        )

        pathMeasure.setPath(path, false)
        val totalLength = pathMeasure.length

        val currentSweepLength = totalLength * sweepAnim.value * visibilityFactor
        val startDistance = totalLength * rotationAnim.value
        val endDistance = startDistance + currentSweepLength

        progressPath.reset()
        if (endDistance > totalLength) {
            pathMeasure.getSegment(
                startDistance,
                totalLength,
                progressPath,
                startWithMoveTo = true,
            )
            pathMeasure.getSegment(
                0f,
                endDistance - totalLength,
                progressPath,
                startWithMoveTo = true,
            )
        } else {
            // 通常ケース
            pathMeasure.getSegment(
                startDistance,
                endDistance,
                progressPath,
                startWithMoveTo = true,
            )
        }

        drawPath(
            path = path,
            color = Color.LightGray.copy(alpha = 0.4f * visibilityFactor),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        )

        drawPath(
            path = progressPath,
            color = color,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        )

        drawContent()
    }
}
