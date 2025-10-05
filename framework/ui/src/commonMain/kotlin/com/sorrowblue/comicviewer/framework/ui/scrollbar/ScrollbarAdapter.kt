package com.sorrowblue.comicviewer.framework.ui.scrollbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
fun ScrollbarBox(
    state: LazyGridState,
    modifier: Modifier = Modifier,
    scrollbarWindowInsets: WindowInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Vertical + WindowInsetsSides.End
    ),
    padding: PaddingValues = PaddingValues(horizontal = 8.dp),
    alignment: Alignment = Alignment.CenterEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = scrollbarStyle(),
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(scrollbarWindowInsets)
                .padding(padding)
                .align(alignment),
        )
    }
}

@Composable
fun ScrollbarBox(
    state: LazyListState,
    modifier: Modifier = Modifier,
    scrollbarWindowInsets: WindowInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Vertical + WindowInsetsSides.End
    ),
    padding: PaddingValues = PaddingValues(end = 8.dp),
    alignment: Alignment = Alignment.CenterEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = scrollbarStyle(),
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(scrollbarWindowInsets)
                .padding(padding)
                .align(alignment),
        )
    }
}

@Composable
fun VerticalScrollbarBox(
    state: ScrollState,
    modifier: Modifier = Modifier,
    scrollbarWindowInsets: WindowInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Vertical + WindowInsetsSides.End
    ),
    padding: PaddingValues = PaddingValues(),
    alignment: Alignment = Alignment.CenterEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = LocalScrollbarStyle.current,
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(scrollbarWindowInsets)
                .padding(padding)
                .align(alignment),
        )
    }
}

/**
 * [CompositionLocal] used to pass [ScrollbarStyle] down the tree.
 * This value is typically set in some "Theme" composable function
 * (DesktopTheme, MaterialTheme)
 */
val LocalScrollbarStyle = staticCompositionLocalOf { defaultScrollbarStyle() }

/**
 * Defines visual style of scrollbars (thickness, shapes, colors, etc).
 * Can be passed as a parameter of scrollbar through [LocalScrollbarStyle]
 */
@Immutable
data class ScrollbarStyle(
    val minimalHeight: Dp,
    val thickness: Dp,
    val shape: Shape,
    val hoverDurationMillis: Int,
    val unhoverColor: Color,
    val hoverColor: Color,
)

private var alertDialogScrollbarStyleCached: ScrollbarStyle? = null

@Composable
fun AlertDialogDefaults.scrollbarStyle(): ScrollbarStyle {
    return alertDialogScrollbarStyleCached ?: ScrollbarStyle(
        minimalHeight = 24.dp,
        thickness = 16.dp,
        shape = RoundedCornerShape(8.dp),
        hoverDurationMillis = 300,
        unhoverColor = ComicTheme.colorScheme.contentColorFor(containerColor).copy(alpha = 0.12f),
        hoverColor = ComicTheme.colorScheme.contentColorFor(containerColor).copy(alpha = 0.50f)
    ).also {
        alertDialogScrollbarStyleCached = it
    }
}

private var scrollbarStyleCached: ScrollbarStyle? = null

@Composable
fun scrollbarStyle() = scrollbarStyleCached ?: ScrollbarStyle(
    minimalHeight = 32.dp,
    thickness = 16.dp,
    shape = RoundedCornerShape(8.dp),
    hoverDurationMillis = 300,
    unhoverColor = ComicTheme.colorScheme.contentColorFor(LocalContainerColor.current)
        .copy(alpha = 0.12f),
    hoverColor = ComicTheme.colorScheme.contentColorFor(LocalContainerColor.current)
        .copy(alpha = 0.50f)
).also {
    scrollbarStyleCached = it
}

/**
 * Simple default [ScrollbarStyle] without applying MaterialTheme.
 */
fun defaultScrollbarStyle() = ScrollbarStyle(
    minimalHeight = 16.dp,
    thickness = 8.dp,
    shape = RoundedCornerShape(4.dp),
    hoverDurationMillis = 300,
    unhoverColor = Color.Black.copy(alpha = 0.12f),
    hoverColor = Color.Black.copy(alpha = 0.50f)
)

/**
 * Defines how to scroll the scrollable component and how to display a scrollbar for it.
 *
 * The values of this interface are typically in pixels, but do not have to be.
 * It's possible to create an adapter with any scroll range of `Double` values.
 */
interface ScrollbarAdapter {

    // We use `Double` values here in order to allow scrolling both very large (think LazyList with
    // millions of items) and very small (think something whose natural coordinates are less than 1)
    // content.

    /**
     * Scroll offset of the content inside the scrollable component.
     *
     * For example, a value of `100` could mean the content is scrolled by 100 pixels from the
     * start.
     */
    val scrollOffset: Double

    /**
     * The size of the scrollable content, on the scrollable axis.
     */
    val contentSize: Double

    /**
     * The size of the viewport, on the scrollable axis.
     */
    val viewportSize: Double

    /**
     * Instantly jump to [scrollOffset].
     *
     * @param scrollOffset target offset to jump to, value will be coerced to the valid
     * scroll range.
     */
    suspend fun scrollTo(scrollOffset: Double)
}

/**
 * The maximum scroll offset of the scrollable content.
 */
val ScrollbarAdapter.maxScrollOffset: Double
    get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class ScrollableScrollbarAdapter(
    private val scrollState: ScrollState,
) : ScrollbarAdapter {

    override val scrollOffset: Double get() = scrollState.value.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.roundToInt())
    }

    override val contentSize: Double
        // This isn't strictly correct, as the actual content can be smaller
        // than the viewport when scrollState.maxValue is 0, but the scrollbar
        // doesn't really care as long as contentSize <= viewportSize; it's
        // just not showing itself
        get() = scrollState.maxValue + viewportSize

    override val viewportSize: Double
        get() = scrollState.viewportSize.toDouble()
}

/**
 * Base class for [LazyListScrollbarAdapter] and [LazyGridScrollbarAdapter],
 * and in the future maybe other lazy widgets that lay out their content in lines.
 */
internal abstract class LazyLineContentAdapter : ScrollbarAdapter {

    // Implement the adapter in terms of "lines", which means either rows,
    // (for a vertically scrollable widget) or columns (for a horizontally
    // scrollable one).
    // For LazyList this translates directly to items; for LazyGrid, it
    // translates to rows/columns of items.

    class VisibleLine(
        val index: Int,
        val offset: Int,
    )

    /**
     * Return the first visible line, if any.
     */
    protected abstract fun firstVisibleLine(): VisibleLine?

    /**
     * Return the total number of lines.
     */
    protected abstract fun totalLineCount(): Int

    /**
     * The sum of content padding (before+after) on the scrollable axis.
     */
    protected abstract fun contentPadding(): Int

    /**
     * Scroll immediately to the given line, and offset it by [scrollOffset] pixels.
     */
    protected abstract suspend fun snapToLine(lineIndex: Int, scrollOffset: Int)

    /**
     * Scroll from the current position by the given amount of pixels.
     */
    protected abstract suspend fun scrollBy(value: Float)

    /**
     * Return the average size (on the scrollable axis) of the visible lines.
     */
    protected abstract fun averageVisibleLineSize(): Double

    /**
     * The spacing between lines.
     */
    protected abstract val lineSpacing: Int

    @JsName("averageVisibleLineSizeProperty")
    private val averageVisibleLineSize by derivedStateOf {
        if (totalLineCount() == 0) {
            0.0
        } else {
            averageVisibleLineSize()
        }
    }

    private val averageVisibleLineSizeWithSpacing get() = averageVisibleLineSize + lineSpacing

    override val scrollOffset: Double
        get() {
            val firstVisibleLine = firstVisibleLine()
            return if (firstVisibleLine == null) {
                0.0
            } else {
                firstVisibleLine.index * averageVisibleLineSizeWithSpacing - firstVisibleLine.offset
            }
        }

    override val contentSize: Double
        get() {
            val totalLineCount = totalLineCount()
            return averageVisibleLineSize * totalLineCount +
                lineSpacing * (totalLineCount - 1).coerceAtLeast(0) +
                contentPadding()
        }

    override suspend fun scrollTo(scrollOffset: Double) {
        val distance = scrollOffset - this@LazyLineContentAdapter.scrollOffset

        // if we scroll less than viewport we need to use scrollBy function to avoid
        // undesirable scroll jumps (when an item size is different)
        //
        // if we scroll more than viewport we should immediately jump to this position
        // without recreating all items between the current and the new position
        if (abs(distance) <= viewportSize) {
            scrollBy(distance.toFloat())
        } else {
            snapTo(scrollOffset)
        }
    }

    private suspend fun snapTo(scrollOffset: Double) {
        val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)

        val index = (scrollOffsetCoerced / averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)
            .coerceAtMost(totalLineCount() - 1)

        val offset = (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)

        snapToLine(lineIndex = index, scrollOffset = offset)
    }
}

internal class LazyListScrollbarAdapter(
    private val scrollState: LazyListState,
) : LazyLineContentAdapter() {

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Vertical) {
                viewportSize.height
            } else {
                viewportSize.width
            }
        }.toDouble()

    /**
     * A heuristic that tries to ignore the "currently stickied" header because it breaks the other
     * computations in this adapter:
     * - The currently stickied header always appears in the list of visible items, with its
     *   regular index. This makes [firstVisibleLine] always return its index, even if the list has
     *   been scrolled far beyond it.
     * - [averageVisibleLineSize] calculates the average size in O(1) by assuming that items don't
     *   overlap, and the stickied item breaks this assumption.
     *
     * Attempts to return the index into `visibleItemsInfo` of the first non-currently-stickied (it
     * could be sticky, but not stickied to the top of the list right now) item, if there is one.
     *
     * Note that this heuristic breaks down if the sticky header covers the entire list, so that
     * it's the only visible item for some portion of the scroll range. But there's currently no
     * known better way to solve it, and it's a relatively unusual case.
     */
    private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo) {
        when (size) {
            0 -> null
            1 -> 0
            else -> {
                val first = this[0]
                val second = this[1]
                // If either the indices or the offsets aren't continuous, then the first item is
                // sticky, so we return 1
                if ((first.index < second.index - 1) ||
                    (first.offset + first.size + lineSpacing > second.offset)
                ) {
                    1
                } else {
                    0
                }
            }
        }
    }

    override fun firstVisibleLine(): VisibleLine? {
        val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
        val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
        return VisibleLine(
            index = firstFloatingItem.index,
            offset = firstFloatingItem.offset
        )
    }

    override fun totalLineCount() = scrollState.layoutInfo.totalItemsCount

    override fun contentPadding() = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(lineIndex, scrollOffset)
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override fun averageVisibleLineSize() = with(scrollState.layoutInfo.visibleItemsInfo) {
        val firstFloatingIndex = firstFloatingVisibleItemIndex() ?: return@with 0.0
        val first = this[firstFloatingIndex]
        val last = last()
        val count = size - firstFloatingIndex
        (last.offset + last.size - first.offset - (count - 1) * lineSpacing).toDouble() / count
    }

    override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

internal class LazyGridScrollbarAdapter(
    private val scrollState: LazyGridState,
) : LazyLineContentAdapter() {

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Vertical) {
                viewportSize.height
            } else {
                viewportSize.width
            }
        }.toDouble()

    private val isVertical = scrollState.layoutInfo.orientation == Vertical

    private val unknownLine = with(LazyGridItemInfo) {
        if (isVertical) UnknownRow else UnknownColumn
    }

    private fun LazyGridItemInfo.line() = if (isVertical) row else column

    private fun LazyGridItemInfo.mainAxisSize() = with(size) {
        if (isVertical) height else width
    }

    private fun LazyGridItemInfo.mainAxisOffset() = with(offset) {
        if (isVertical) y else x
    }

    private fun lineOfIndex(index: Int) = index / scrollState.layoutInfo.maxSpan

    private fun indexOfFirstInLine(line: Int) = line * scrollState.layoutInfo.maxSpan

    override fun firstVisibleLine(): VisibleLine? {
        return scrollState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.line() != unknownLine } // Skip exiting items
            ?.let { firstVisibleItem ->
                VisibleLine(
                    index = firstVisibleItem.line(),
                    offset = firstVisibleItem.mainAxisOffset()
                )
            }
    }

    override fun totalLineCount(): Int {
        val itemCount = scrollState.layoutInfo.totalItemsCount
        return if (itemCount == 0) {
            0
        } else {
            lineOfIndex(itemCount - 1) + 1
        }
    }

    override fun contentPadding() = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(
            index = indexOfFirstInLine(lineIndex),
            scrollOffset = scrollOffset
        )
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    private val lineIsKnown = { itemInfo: LazyGridItemInfo -> itemInfo.line() != unknownLine }

    override fun averageVisibleLineSize(): Double {
        val visibleItemsInfo = scrollState.layoutInfo.visibleItemsInfo

        // First and last visible, non-exiting LazyGridItemInfo
        val first = visibleItemsInfo.firstOrNull(lineIsKnown) ?: return 0.0
        val last = visibleItemsInfo.last(lineIsKnown)

        // Compute the size (e.g. height for vertical grid) of the last line
        val lastLine = last.line()
        val lastLineSize = visibleItemsInfo
            .asReversed()
            .asSequence()
            .filter(lineIsKnown)
            .takeWhile { it.line() == lastLine }
            .maxOf { it.mainAxisSize() }

        val lineCount = last.line() - first.line() + 1
        val lineSpacingSum = (lineCount - 1) * lineSpacing
        return (
            last.mainAxisOffset() + lastLineSize - first.mainAxisOffset() - lineSpacingSum
            ).toDouble() / lineCount
    }

    override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

internal class SliderAdapter(
    val adapter: ScrollbarAdapter,
    private val trackSize: Int,
    private val minHeight: Float,
    private val reverseLayout: Boolean,
    private val isVertical: Boolean,
    private val coroutineScope: CoroutineScope,
) {

    private val contentSize get() = adapter.contentSize
    private val visiblePart: Double
        get() {
            val contentSize = contentSize
            return if (contentSize == 0.0) {
                1.0
            } else {
                (adapter.viewportSize / contentSize).coerceAtMost(1.0)
            }
        }

    val thumbSize
        get() = (trackSize * visiblePart).coerceAtLeast(minHeight.toDouble())

    private val scrollScale: Double
        get() {
            val extraScrollbarSpace = trackSize - thumbSize
            val extraContentSpace = adapter.maxScrollOffset // == contentSize - viewportSize
            return if (extraContentSpace == 0.0) 1.0 else extraScrollbarSpace / extraContentSpace
        }

    private val rawPosition: Double
        get() = scrollScale * adapter.scrollOffset

    val position: Double
        get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition

    val bounds get() = position..position + thumbSize

    // How much of the current drag was ignored because we've reached the end of the scrollbar area
    private var unscrolledDragDistance = 0.0

    /** Called when the thumb dragging starts */
    fun onDragStarted() {
        unscrolledDragDistance = 0.0
    }

    private suspend fun setPosition(value: Double) {
        val rawPosition = if (reverseLayout) {
            trackSize - thumbSize - value
        } else {
            value
        }
        adapter.scrollTo(rawPosition / scrollScale)
    }

    private val dragMutex = Mutex()

    /** Called on every movement while dragging the thumb */
    fun onDragDelta(offset: Offset) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            // Mutex is used to ensure that all earlier drag deltas were applied
            // before calculating a new raw position
            dragMutex.withLock {
                val dragDelta = if (isVertical) offset.y else offset.x
                val maxScrollPosition = adapter.maxScrollOffset * scrollScale
                val currentPosition = position
                val targetPosition =
                    (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(
                        0.0,
                        maxScrollPosition
                    )
                val sliderDelta = targetPosition - currentPosition

                // Have to add to position for smooth content scroll if the items are of different size
                val newPos = position + sliderDelta
                setPosition(newPos)
                unscrolledDragDistance += dragDelta - sliderDelta
            }
        }
    }
}

/**
 * Vertical scrollbar that can be attached to some scrollable
 * component (ScrollableColumn, LazyColumn) and share common state with it.
 *
 * Can be placed independently.
 *
 * Example:
 *     val state = rememberScrollState(0)
 *
 *     Box(Modifier.fillMaxSize()) {
 *         Box(modifier = Modifier.verticalScroll(state)) {
 *             ...
 *         }
 *
 *         VerticalScrollbar(
 *             adapter = rememberScrollbarAdapter(state)
 *             modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
 *         )
 *     }
 *
 * @param adapter [com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarAdapter] that will be used to
 * communicate with scrollable component
 * @param modifier the modifier to apply to this layout
 * @param reverseLayout reverse the direction of scrolling and layout, when `true`
 * and [LazyListState.firstVisibleItemIndex] == 0 then scrollbar
 * will be at the bottom of the container.
 * It is usually used in pair with `LazyColumn(reverseLayout = true)`
 * @param style [ScrollbarStyle] to define visual style of scrollbar
 * @param interactionSource [MutableInteractionSource] that will be used to dispatch
 * [DragInteraction.Start] when this Scrollbar is being dragged.
 */
@Composable
fun VerticalScrollbar(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = Scrollbar(
    adapter = adapter,
    scrollbarAdapterFactory = DefaultScrollbarAdapterFactory,
    reverseLayout = reverseLayout,
    style = style,
    interactionSource = interactionSource,
    isVertical = true,
    modifier = modifier
)

/**
 * Horizontal scrollbar that can be attached to some scrollable
 * component (Modifier.verticalScroll(), LazyRow) and share common state with it.
 *
 * Can be placed independently.
 *
 * Example:
 *     val state = rememberScrollState(0)
 *
 *     Box(Modifier.fillMaxSize()) {
 *         Box(modifier = Modifier.verticalScroll(state)) {
 *             ...
 *         }
 *
 *         HorizontalScrollbar(
 *             adapter = rememberScrollbarAdapter(state)
 *             modifier = Modifier.align(Alignment.CenterEnd).fillMaxWidth(),
 *         )
 *     }
 *
 * @param adapter [com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarAdapter] that will be used to
 * communicate with scrollable component
 * @param modifier the modifier to apply to this layout
 * @param reverseLayout reverse the direction of scrolling and layout, when `true`
 * and [LazyListState.firstVisibleItemIndex] == 0 then scrollbar
 * will be at the end of the container.
 * It is usually used in pair with `LazyRow(reverseLayout = true)`
 * @param style [ScrollbarStyle] to define visual style of scrollbar
 * @param interactionSource [MutableInteractionSource] that will be used to dispatch
 * [DragInteraction.Start] when this Scrollbar is being dragged.
 */
@Composable
fun HorizontalScrollbar(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = Scrollbar(
    adapter = adapter,
    scrollbarAdapterFactory = DefaultScrollbarAdapterFactory,
    reverseLayout = if (LocalLayoutDirection.current == LayoutDirection.Rtl) !reverseLayout else reverseLayout,
    style = style,
    interactionSource = interactionSource,
    isVertical = false,
    modifier = modifier
)

private typealias ScrollbarAdapterFactory<T> = (
    adapter: T,
    trackSize: Int,
) -> ScrollbarAdapter

private val DefaultScrollbarAdapterFactory: ScrollbarAdapterFactory<ScrollbarAdapter> =
    { adapter, _ -> adapter }

/**
 * The actual implementation of the scrollbar.
 *
 * Takes the scroll adapter (old or new) and a function that converts it to the new scrollbar
 * adapter interface. This allows both the old (left for backwards compatibility) and new
 * implementations to use the same code.
 */
@Composable
internal fun Scrollbar(
    adapter: ScrollbarAdapter,
    // We need an adapter factory because we can't convert an old to a new
    // adapter until we have the track/container size
    scrollbarAdapterFactory: ScrollbarAdapterFactory<ScrollbarAdapter>,
    reverseLayout: Boolean,
    style: ScrollbarStyle,
    interactionSource: MutableInteractionSource,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
) = with(LocalDensity.current) {
    val dragInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
    DisposableEffect(interactionSource) {
        onDispose {
            dragInteraction.value?.let { interaction ->
                interactionSource.tryEmit(DragInteraction.Cancel(interaction))
                dragInteraction.value = null
            }
        }
    }

    var containerSize by remember { mutableIntStateOf(0) }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val isHighlighted by remember {
        derivedStateOf {
            isHovered || dragInteraction.value is DragInteraction.Start
        }
    }

    val minimalHeight = style.minimalHeight.toPx()

    val scrollbarAdapter = remember(adapter, containerSize) {
        scrollbarAdapterFactory(adapter, containerSize)
    }
    val coroutineScope = rememberCoroutineScope()
    val sliderAdapter = remember(
        scrollbarAdapter,
        containerSize,
        minimalHeight,
        reverseLayout,
        isVertical,
        coroutineScope
    ) {
        SliderAdapter(
            scrollbarAdapter,
            containerSize,
            minimalHeight,
            reverseLayout,
            isVertical,
            coroutineScope
        )
    }

    val scrollThickness = style.thickness.roundToPx()
    val measurePolicy = if (isVertical) {
        remember(sliderAdapter, scrollThickness) {
            verticalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    } else {
        remember(sliderAdapter, scrollThickness) {
            horizontalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    }

    val color by animateColorAsState(
        if (isHighlighted) style.hoverColor else style.unhoverColor,
        animationSpec = TweenSpec(durationMillis = style.hoverDurationMillis)
    )

    val isVisible = sliderAdapter.thumbSize < containerSize

    Layout(
        {
            Box(
                Modifier
                    .background(if (isVisible) color else Color.Transparent, style.shape)
                    .scrollbarDrag(
                        interactionSource = interactionSource,
                        draggedInteraction = dragInteraction,
                        sliderAdapter = sliderAdapter,
                    )
            )
        },
        modifier
            .hoverable(interactionSource = interactionSource)
            .scrollOnPressTrack(isVertical, reverseLayout, sliderAdapter),
        measurePolicy
    )
}

/**
 * Create and [remember] [com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarAdapter] for
 * scrollable container with the given instance [ScrollState].
 */
@JvmName("rememberScrollbarAdapter2")
@Composable
fun rememberScrollbarAdapter(
    scrollState: ScrollState,
): ScrollbarAdapter = remember(scrollState) {
    ScrollbarAdapter(scrollState)
}

/**
 * Create and [remember] [com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarAdapter] for
 * lazy scrollable container with the given instance [LazyListState].
 */
@JvmName("rememberScrollbarAdapter2")
@Composable
fun rememberScrollbarAdapter(
    scrollState: LazyListState,
): ScrollbarAdapter = remember(scrollState) {
    ScrollbarAdapter(scrollState)
}

/**
 * Create and [remember] [com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarAdapter] for lazy grid with
 * the given instance of [LazyGridState].
 */
@JvmName("rememberScrollbarAdapter2")
@Composable
fun rememberScrollbarAdapter(
    scrollState: LazyGridState,
): ScrollbarAdapter = remember(scrollState) {
    ScrollbarAdapter(scrollState)
}

/**
 * ScrollbarAdapter for Modifier.verticalScroll and Modifier.horizontalScroll
 *
 * [scrollState] is instance of [ScrollState] which is used by scrollable component
 *
 * Example:
 *     val state = rememberScrollState(0)
 *
 *     Box(Modifier.fillMaxSize()) {
 *         Box(modifier = Modifier.verticalScroll(state)) {
 *             ...
 *         }
 *
 *         VerticalScrollbar(
 *             adapter = rememberScrollbarAdapter(state)
 *             modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
 *         )
 *     }
 */
private fun ScrollbarAdapter(
    scrollState: ScrollState,
): ScrollbarAdapter =
    ScrollableScrollbarAdapter(scrollState)

/**
 * ScrollbarAdapter for lazy lists.
 *
 * [scrollState] is instance of [LazyListState] which is used by scrollable component
 *
 * Example:
 *
 *     Box(Modifier.fillMaxSize()) {
 *         val state = rememberLazyListState()
 *
 *         LazyColumn(state = state) {
 *             ...
 *         }
 *
 *         VerticalScrollbar(
 *             adapter = rememberScrollbarAdapter(state)
 *             modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
 *         )
 *     }
 */
private fun ScrollbarAdapter(
    scrollState: LazyListState,
): ScrollbarAdapter =
    LazyListScrollbarAdapter(scrollState)

/**
 * ScrollbarAdapter for lazy grids.
 *
 * [scrollState] is instance of [LazyGridState] which is used by scrollable component
 *
 * Example:
 *
 *     Box(Modifier.fillMaxSize()) {
 *         val state = rememberLazyGridState()
 *
 *         LazyVerticalGrid(columns = ..., state = state) {
 *             ...
 *         }
 *
 *         VerticalScrollbar(
 *             adapter = rememberScrollbarAdapter(state)
 *             modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
 *         )
 *     }
 */
private fun ScrollbarAdapter(
    scrollState: LazyGridState,
): ScrollbarAdapter =
    LazyGridScrollbarAdapter(scrollState)

private val SliderAdapter.thumbPixelRange: IntRange
    get() {
        val start = position.roundToInt()
        val endExclusive = start + thumbSize.roundToInt()

        return (start until endExclusive)
    }

private val IntRange.size get() = last + 1 - first

private fun verticalMeasurePolicy(
    sliderAdapter: SliderAdapter,
    setContainerSize: (Int) -> Unit,
    scrollThickness: Int,
) = MeasurePolicy { measurables, constraints ->
    setContainerSize(constraints.maxHeight)
    val pixelRange = sliderAdapter.thumbPixelRange
    val placeable = measurables.first().measure(
        Constraints.fixed(
            constraints.constrainWidth(scrollThickness),
            pixelRange.size
        )
    )
    layout(placeable.width, constraints.maxHeight) {
        placeable.place(0, pixelRange.first)
    }
}

private fun horizontalMeasurePolicy(
    sliderAdapter: SliderAdapter,
    setContainerSize: (Int) -> Unit,
    scrollThickness: Int,
) = MeasurePolicy { measurables, constraints ->
    setContainerSize(constraints.maxWidth)
    val pixelRange = sliderAdapter.thumbPixelRange
    val placeable = measurables.first().measure(
        Constraints.fixed(
            pixelRange.size,
            constraints.constrainHeight(scrollThickness)
        )
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(pixelRange.first, 0)
    }
}

/**
 * Responsible for scrolling when the scrollbar track is pressed (outside the thumb).
 */
internal class TrackPressScroller(
    private val coroutineScope: CoroutineScope,
    private val sliderAdapter: SliderAdapter,
    private val reverseLayout: Boolean,
) {

    /**
     * The current direction of scroll (1: down/right, -1: up/left, 0: not scrolling)
     */
    private var direction = 0

    /**
     * The currently pressed location (in pixels) on the scrollable axis.
     */
    private var offset: Float? = null

    /**
     * The job that keeps scrolling while the track is pressed.
     */
    private var job: Job? = null

    /**
     * Calculates the direction of scrolling towards the given offset (in pixels).
     */
    private fun directionOfScrollTowards(offset: Float): Int {
        val pixelRange = sliderAdapter.thumbPixelRange
        return when {
            offset < pixelRange.first -> if (reverseLayout) 1 else -1
            offset > pixelRange.last -> if (reverseLayout) -1 else 1
            else -> 0
        }
    }

    /**
     * Scrolls once towards the current offset, if it matches the direction of the current gesture.
     */
    private suspend fun scrollTowardsCurrentOffset() {
        offset?.let {
            val currentDirection = directionOfScrollTowards(it)
            if (currentDirection != direction) {
                return
            }
            with(sliderAdapter.adapter) {
                scrollTo(scrollOffset + currentDirection * viewportSize)
            }
        }
    }

    /**
     * Starts the job that scrolls continuously towards the current offset.
     */
    private fun startScrolling() {
        job?.cancel()
        job = coroutineScope.launch {
            scrollTowardsCurrentOffset()
            delay(DelayBeforeSecondScrollOnTrackPress)
            while (true) {
                scrollTowardsCurrentOffset()
                delay(DelayBetweenScrollsOnTrackPress)
            }
        }
    }

    /**
     * Invoked on the first press for a gesture.
     */
    fun onPress(offset: Float) {
        this.offset = offset
        this.direction = directionOfScrollTowards(offset)

        if (direction != 0) {
            startScrolling()
        }
    }

    /**
     * Invoked when the pointer moves while pressed during the gesture.
     */
    fun onMovePressed(offset: Float) {
        this.offset = offset
    }

    /**
     * Cleans up when the gesture finishes.
     */
    private fun cleanupAfterGesture() {
        job?.cancel()
        direction = 0
        offset = null
    }

    /**
     * Invoked when the button is released.
     */
    fun onRelease() {
        cleanupAfterGesture()
    }

    /**
     * Invoked when the gesture is cancelled.
     */
    fun onGestureCancelled() {
        cleanupAfterGesture()
        // Maybe revert to the initial position?
    }
}

/**
 * The delay between the 1st and 2nd scroll while the scrollbar track is pressed outside the thumb.
 */
private const val DelayBeforeSecondScrollOnTrackPress: Long = 300L

/**
 * The delay between each subsequent (after the 2nd) scroll while the scrollbar track is pressed
 * outside the thumb.
 */
private const val DelayBetweenScrollsOnTrackPress: Long = 100L
