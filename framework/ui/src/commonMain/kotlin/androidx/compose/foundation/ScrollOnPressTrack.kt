package androidx.compose.foundation

import androidx.compose.foundation.v2.SliderAdapter
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastFirstOrNull

internal fun Modifier.scrollOnPressTrack(
    isVertical: Boolean,
    reverseLayout: Boolean,
    sliderAdapter: SliderAdapter,
): Modifier = this.then(ScrollOnPressTrackElement(isVertical, reverseLayout, sliderAdapter))

private data class ScrollOnPressTrackElement(
    private val isVertical: Boolean,
    private val reverseLayout: Boolean,
    private val sliderAdapter: SliderAdapter,
) : ModifierNodeElement<ScrollOnPressTrackNode>() {
    override fun create(): ScrollOnPressTrackNode {
        return ScrollOnPressTrackNode(isVertical, sliderAdapter, reverseLayout)
    }

    override fun update(node: ScrollOnPressTrackNode) {
        node.update(isVertical, sliderAdapter, reverseLayout)
    }
}

private class ScrollOnPressTrackNode(
    private var isVertical: Boolean,
    private var sliderAdapter: SliderAdapter,
    private var reverseLayout: Boolean,
) : Modifier.Node(), PointerInputModifierNode {

    var scroller: TrackPressScroller? = null
    var buttonPressed = false
    fun update(isVertical: Boolean, sliderAdapter: SliderAdapter, reverseLayout: Boolean) {
        this.isVertical = isVertical
        this.sliderAdapter = sliderAdapter
        this.reverseLayout = reverseLayout
        scroller = TrackPressScroller(coroutineScope, sliderAdapter, reverseLayout)
    }

    override fun onAttach() {
        super.onAttach()
        scroller = TrackPressScroller(coroutineScope, sliderAdapter, reverseLayout)
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        when (pointerEvent.type) {
            PointerEventType.Press -> {
                if (!buttonPressed) {
                    buttonPressed = true
                    scroller?.onPress(pointerEvent.changes.first().position.onScrollAxis())
                    return
                }
            }

            PointerEventType.Move -> {
                val inputChange = pointerEvent.changes.fastFirstOrNull { change ->
                    change.positionChangeIgnoreConsumed().run {
                        if (isVertical) y != 0f else x != 0f
                    }
                }
                if (inputChange == null) {
                    scroller?.onGestureCancelled()
                } else if (!inputChange.pressed) {
                    scroller?.onRelease()
                } else {
                    scroller?.onMovePressed(inputChange.position.onScrollAxis())
                }
            }

            PointerEventType.Release -> {
                if (buttonPressed) {
                    buttonPressed = false
                    scroller?.onRelease()
                }
            }
        }
    }

    override fun onCancelPointerInput() {
        buttonPressed = false
        scroller?.onGestureCancelled()
    }

    private fun Offset.onScrollAxis() = if (isVertical) y else x
}
