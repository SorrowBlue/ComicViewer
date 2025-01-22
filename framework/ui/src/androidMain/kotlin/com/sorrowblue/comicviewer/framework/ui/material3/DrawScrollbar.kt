package com.sorrowblue.comicviewer.framework.ui.material3

import android.view.ViewConfiguration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.util.Objects
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

fun Modifier.drawVerticalScrollbar(
    state: ScrollState,
    reverseScrolling: Boolean = false,
) = this then DrawScrollbarElement(state, Orientation.Vertical, reverseScrolling)

fun Modifier.drawHorizontalScrollbar(
    state: ScrollState,
    reverseScrolling: Boolean = false,
) = this then DrawScrollbarElement(state, Orientation.Horizontal, reverseScrolling)

private class DrawScrollbarElement(
    private val state: ScrollState,
    private val orientation: Orientation,
    private val isReverseScroll: Boolean,
) : ModifierNodeElement<DrawScrollbarNodeImpl>() {

    override fun create(): DrawScrollbarNodeImpl {
        return DrawScrollbarNodeImpl(state, orientation, isReverseScroll)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DrawScrollbarElement) return false
        return orientation == other.orientation &&
            isReverseScroll == other.isReverseScroll
    }

    override fun hashCode(): Int {
        return Objects.hash(orientation, isReverseScroll)
    }

    override fun update(node: DrawScrollbarNodeImpl) {
        node.update(orientation, isReverseScroll)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DrawScrollbar"
        properties["orientation"] = orientation
        properties["isReverseScroll"] = isReverseScroll
    }
}

private class DrawScrollbarNodeImpl(
    private val state: ScrollState,
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
        if (state.maxValue > 0) {
            val canvasSize = if (orientation == Orientation.Horizontal) size.width else size.height
            val totalSize = canvasSize + state.maxValue
            val thumbSize = canvasSize / totalSize * canvasSize
            val startOffset = state.value / totalSize * canvasSize
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

fun interface ScrollbarDrawer {
    fun DrawScope.onDraw(reverseDirection: Boolean, atEnd: Boolean, color: Color, alpha: Float)
}

fun DrawScope.drawScrollbar(
    orientation: Orientation,
    reverseDirection: Boolean,
    atEnd: Boolean,
    color: Color,
    alpha: Float,
    thumbSize: Float,
    startOffset: Float,
) {
    val thicknessPx = Thickness.toPx()
    val topLeft = if (orientation == Orientation.Horizontal) {
        Offset(
            if (reverseDirection) size.width - startOffset - thumbSize else startOffset,
            if (atEnd) size.height - thicknessPx else 0f
        )
    } else {
        Offset(
            if (atEnd) size.width - thicknessPx else 0f,
            if (reverseDirection) size.height - startOffset - thumbSize else startOffset
        )
    }
    val size = if (orientation == Orientation.Horizontal) {
        Size(thumbSize, thicknessPx)
    } else {
        Size(thicknessPx, thumbSize)
    }

    drawRect(
        color = color,
        topLeft = topLeft,
        size = size,
        alpha = alpha
    )
}

fun drawScrollbarNode(
    orientation: Orientation,
    isReverseScrolling: Boolean,
    scrollbarDrawer: ScrollbarDrawer,
): IDrawScrollbarNode {
    return DrawScrollbarNode(orientation, isReverseScrolling, scrollbarDrawer)
}

interface IDrawScrollbarNode : DelegatableNode {

    fun update(orientation: Orientation, isReverseScrolling: Boolean)
}

private class DrawScrollbarNode(
    private var orientation: Orientation,
    private var isReverseScrolling: Boolean,
    private val scrollbarDrawer: ScrollbarDrawer,
) : DelegatingNode(),
    IDrawScrollbarNode,
    DrawModifierNode,
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {

    private val scrolled = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val nestedScrollDispatcher = NestedScrollDispatcher()
    private val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val delta = if (orientation == Orientation.Horizontal) consumed.x else consumed.y
            if (delta != 0f) scrolled.tryEmit(Unit)
            return Offset.Zero
        }
    }

    override fun update(orientation: Orientation, isReverseScrolling: Boolean) {
        this.orientation = orientation
        this.isReverseScrolling = isReverseScrolling
    }

    init {
        delegate(nestedScrollModifierNode(nestedScrollConnection, nestedScrollDispatcher))
    }

    private val alpha = Animatable(0f)
    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            scrolled.collectLatest {
                alpha.snapTo(1f)
                delay(ViewConfiguration.getScrollDefaultDelay().toLong())
                alpha.animateTo(0f, animationSpec = FadeOutAnimationSpec)
            }
        }
    }

    var isLtr = true

    override fun onObservedReadsChanged() {
        logcat { "onObservedReadsChanged" }
        observeReads {
            logcat { "observeReads" }
            val isLtr = currentValueOf(LocalLayoutDirection) == LayoutDirection.Ltr
            if (this.isLtr != isLtr) {
                this.isLtr = isLtr
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val isLtr = currentValueOf(LocalLayoutDirection) == LayoutDirection.Ltr
        val reverseDirection = if (orientation == Orientation.Horizontal) {
            if (isLtr) isReverseScrolling else !isReverseScrolling
        } else {
            isReverseScrolling
        }
        val atEnd = if (orientation == Orientation.Vertical) isLtr else true
        val color = currentValueOf(LocalContentColor).copy(alpha = 0.5f)
        with(scrollbarDrawer) {
            onDraw(reverseDirection, atEnd, color, alpha.value)
        }
    }
}

private val Thickness = 4.dp
private val FadeOutAnimationSpec =
    tween<Float>(durationMillis = ViewConfiguration.getScrollBarFadeDuration())
