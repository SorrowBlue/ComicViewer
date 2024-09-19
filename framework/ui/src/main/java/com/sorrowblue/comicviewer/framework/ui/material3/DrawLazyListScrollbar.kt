package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.util.fastSumBy
import java.util.Objects

fun Modifier.drawVerticalScrollbar(
    state: LazyListState,
    reverseScrolling: Boolean = false,
) = this then DrawLazyListScrollbarElement(state, Orientation.Vertical, reverseScrolling)

fun Modifier.drawHorizontalScrollbar(
    state: LazyListState,
    reverseScrolling: Boolean = false,
) = this then DrawLazyListScrollbarElement(state, Orientation.Horizontal, reverseScrolling)

private class DrawLazyListScrollbarElement(
    private val state: LazyListState,
    private val orientation: Orientation,
    private val isReverseScroll: Boolean,
) : ModifierNodeElement<DrawLazyListScrollbarNode>() {

    override fun create(): DrawLazyListScrollbarNode {
        return DrawLazyListScrollbarNode(state, orientation, isReverseScroll)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DrawLazyListScrollbarElement) return false
        return orientation == other.orientation &&
            isReverseScroll == other.isReverseScroll
    }

    override fun hashCode(): Int {
        return Objects.hash(orientation, isReverseScroll)
    }

    override fun update(node: DrawLazyListScrollbarNode) {
        node.update(orientation, isReverseScroll)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DrawLazyGridScrollbar"
        properties["orientation"] = orientation
        properties["isReverseScroll"] = isReverseScroll
    }
}

private class DrawLazyListScrollbarNode(
    private val state: LazyListState,
    private var orientation: Orientation,
    private var isReverseScrolling: Boolean,
) : DelegatingNode() {
    fun update(
        orientation: Orientation,
        reverseScroll: Boolean,
    ) {
        this.isReverseScrolling = reverseScroll
        this.orientation = orientation
        drawScrollbarNode.update(orientation, reverseScroll)
    }

    val scrollbarDrawer = ScrollbarDrawer { reverseDirection, atEnd, color, alpha ->
        val layoutInfo = state.layoutInfo
        val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val items = layoutInfo.visibleItemsInfo
        val itemsSize = items.fastSumBy { it.size }
        if (items.size < layoutInfo.totalItemsCount || itemsSize > viewportSize) {
            val estimatedItemSize = if (items.isEmpty()) 0f else itemsSize.toFloat() / items.size
            val totalSize = estimatedItemSize * layoutInfo.totalItemsCount
            val canvasSize = if (orientation == Orientation.Horizontal) size.width else size.height
            val thumbSize = viewportSize / totalSize * canvasSize
            val startOffset = if (items.isEmpty()) {
                0f
            } else {
                items.first().run {
                    (estimatedItemSize * index - offset) / totalSize * canvasSize
                }
            }
            drawScrollbar(
                orientation,
                reverseDirection,
                atEnd,
                color,
                alpha,
                thumbSize - layoutInfo.beforeContentPadding,
                startOffset + layoutInfo.beforeContentPadding
            )
        }
    }

    private val drawScrollbarNode =
        delegate(drawScrollbarNode(orientation = orientation, isReverseScrolling, scrollbarDrawer))
}
