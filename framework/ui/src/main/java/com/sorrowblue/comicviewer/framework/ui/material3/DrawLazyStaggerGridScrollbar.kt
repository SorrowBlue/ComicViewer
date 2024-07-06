package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import java.util.Objects

fun Modifier.drawVerticalScrollbar(
    state: LazyStaggeredGridState,
    spanCount: Int,
    reverseScrolling: Boolean = false,
) = this then DrawLazyStaggeredGridScrollbarElement(
    state,
    spanCount,
    Orientation.Vertical,
    reverseScrolling
)

private class DrawLazyStaggeredGridScrollbarElement(
    private val state: LazyStaggeredGridState,
    private val spanCount: Int,
    private val orientation: Orientation,
    private val isReverseScroll: Boolean,
) : ModifierNodeElement<DrawLazyStaggeredGridScrollbarNode>() {

    override fun create(): DrawLazyStaggeredGridScrollbarNode {
        return DrawLazyStaggeredGridScrollbarNode(state, spanCount, orientation, isReverseScroll)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DrawLazyStaggeredGridScrollbarElement) return false
        return spanCount == other.spanCount &&
            orientation == other.orientation &&
            isReverseScroll == other.isReverseScroll
    }

    override fun hashCode(): Int {
        return Objects.hash(spanCount, orientation, isReverseScroll)
    }

    override fun update(node: DrawLazyStaggeredGridScrollbarNode) {
        node.update(spanCount, orientation, isReverseScroll)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DrawLazyGridScrollbar"
        properties["spanCount"] = spanCount
        properties["orientation"] = orientation
        properties["isReverseScroll"] = isReverseScroll
    }
}

private class DrawLazyStaggeredGridScrollbarNode(
    private val state: LazyStaggeredGridState,
    private var spanCount: Int,
    private var orientation: Orientation,
    private var isReverseScrolling: Boolean,
) : DelegatingNode() {
    fun update(
        spanCount: Int,
        orientation: Orientation,
        reverseScroll: Boolean,
    ) {
        this.spanCount = spanCount
        this.isReverseScrolling = reverseScroll
        this.orientation = orientation
        drawScrollbarNode.update(orientation, reverseScroll)
    }

    val scrollbarDrawer = ScrollbarDrawer { reverseDirection, atEnd, color, alpha ->
        val layoutInfo = state.layoutInfo
        val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val items = layoutInfo.visibleItemsInfo
        val rowCount = (items.size + spanCount - 1) / spanCount
        var itemsSize = layoutInfo.afterContentPadding + layoutInfo.beforeContentPadding
        for (i in 0 until rowCount) {
            itemsSize += if (orientation == Orientation.Vertical) {
                items[i * spanCount].size.height + if (i == 0) 0 else layoutInfo.mainAxisItemSpacing
            } else {
                items[i * spanCount].size.width + if (i == 0) 0 else layoutInfo.mainAxisItemSpacing
            }
        }
        if (items.size < layoutInfo.totalItemsCount || itemsSize > viewportSize) {
            val estimatedItemSize = if (rowCount == 0) 0f else itemsSize.toFloat() / rowCount
            val totalRow = (layoutInfo.totalItemsCount + spanCount - 1) / spanCount
            val totalSize = estimatedItemSize * totalRow
            val canvasSize = if (orientation == Orientation.Vertical) size.height else size.width
            val thumbSize = viewportSize / totalSize * canvasSize
            val startOffset = if (rowCount == 0) {
                0f
            } else {
                items.first().run {
                    val rowIndex = index / spanCount
                    val offset = if (orientation == Orientation.Vertical) offset.y else offset.x
                    (estimatedItemSize * rowIndex - offset) / totalSize * canvasSize
                }
            }
            drawScrollbar(
                orientation,
                reverseDirection,
                atEnd,
                color,
                alpha,
                thumbSize,
                startOffset
            )
        }
    }

    private val drawScrollbarNode =
        delegate(drawScrollbarNode(orientation = orientation, isReverseScrolling, scrollbarDrawer))
}
