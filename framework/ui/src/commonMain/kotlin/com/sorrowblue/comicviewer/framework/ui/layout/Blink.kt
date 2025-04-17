package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.blink(
    color: Color,
    range: ClosedFloatingPointRange<Float> = 0.0f..0.5f,
    shape: Shape = ShapeDefaults.Medium,
): Modifier = this then DrawBlinkElement(color, range, shape)

private data class DrawBlinkElement(
    val color: Color,
    val range: ClosedFloatingPointRange<Float>,
    val shape: Shape,
) : ModifierNodeElement<DrawBlinkNode>() {
    override fun create() = DrawBlinkNode(color, range, shape)

    override fun update(node: DrawBlinkNode) {
        node.update(color, range, shape)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "blink"
        properties["color"] = color
        properties["range"] = range
        properties["shape"] = shape
    }
}

private class DrawBlinkNode(
    var color: Color = Color.Red,
    var range: ClosedFloatingPointRange<Float>,
    var shape: Shape,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val alpha = Animatable(range.endInclusive)
    private val count = mutableIntStateOf(0)
    private var job: Job? = null

    override fun onAttach() {
        super.onAttach()
        update()
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        with(
            currentValueOf(LocalDensity)
        ) {
            drawRoundRect(
                color = Color.Red,
                size = size,
                alpha = alpha.value,
                cornerRadius = CornerRadius(
                    (shape as RoundedCornerShape).topStart.toPx(size, this),
                    (shape as RoundedCornerShape).topStart.toPx(size, this)
                )
            )
        }
    }

    fun update(color: Color, range: ClosedFloatingPointRange<Float>, shape: Shape) {
        if (this.range != range || this.color != color || this.shape != shape) {
            update()
            this.range = range
            this.color = color
            this.shape = shape
        }
    }

    private fun update() {
        job?.cancel()
        count.intValue = 0
        job = coroutineScope.launch {
            while (count.intValue < 5) {
                delay(500)
                alpha.animateTo(range.start)
                delay(500)
                alpha.animateTo(range.endInclusive)
                count.intValue++
            }
            delay(500)
            alpha.animateTo(0f)
        }
    }
}
