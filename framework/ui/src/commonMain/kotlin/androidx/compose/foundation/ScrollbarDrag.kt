package androidx.compose.foundation

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.v2.SliderAdapter
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal fun Modifier.scrollbarDrag(
    interactionSource: MutableInteractionSource,
    draggedInteraction: MutableState<DragInteraction.Start?>,
    sliderAdapter: SliderAdapter,
): Modifier = this.then(ScrollbarDragElement(interactionSource, draggedInteraction, sliderAdapter))

private data class ScrollbarDragElement(
    private val interactionSource: MutableInteractionSource,
    private val draggedInteraction: MutableState<DragInteraction.Start?>,
    private val sliderAdapter: SliderAdapter,
) : ModifierNodeElement<ScrollbarDragNode>() {
    override fun create(): ScrollbarDragNode {
        return ScrollbarDragNode(interactionSource, draggedInteraction, sliderAdapter)
    }

    override fun update(node: ScrollbarDragNode) {
        node.interactionSource = interactionSource
        node.draggedInteraction = draggedInteraction
        node.sliderAdapter = sliderAdapter
    }
}

private class ScrollbarDragNode(
    var interactionSource: MutableInteractionSource,
    var draggedInteraction: MutableState<DragInteraction.Start?>,
    var sliderAdapter: SliderAdapter,
) : Modifier.Node(), PointerInputModifierNode {

    var buttonPressed = false
    var interaction: DragInteraction? = null

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        when (pointerEvent.type) {
            PointerEventType.Press -> {
                if (buttonPressed) return
                buttonPressed = true
                val interaction = DragInteraction.Start()
                this.interaction = interaction
                interactionSource.tryEmit(interaction)
                draggedInteraction.value = interaction
                sliderAdapter.onDragStarted()
            }

            PointerEventType.Move -> {
                pointerEvent.changes.forEach { change ->
                    sliderAdapter.onDragDelta(change.positionChange())
                    change.consume()
                }
            }

            PointerEventType.Release -> {
                interaction?.let {
                    if (it is DragInteraction.Start) {
                        interactionSource.tryEmit(DragInteraction.Stop(it))
                    }
                }
                buttonPressed = false
                draggedInteraction.value = null
            }
        }
    }

    override fun onCancelPointerInput() {
        interaction?.let {
            if (it is DragInteraction.Start) {
                interactionSource.tryEmit(DragInteraction.Cancel(it))
            }
        }
        buttonPressed = false
        draggedInteraction.value = null
    }
}
