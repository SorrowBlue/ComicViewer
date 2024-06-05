package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.Dimension
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo

@Composable
fun PaddingValues.asWindowInsets(localLayoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    WindowInsets(
        left = calculateLeftPadding(localLayoutDirection),
        top = calculateTopPadding(),
        right = calculateRightPadding(localLayoutDirection),
        bottom = calculateBottomPadding()
    )

@Composable
fun PaddingValues.add(
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
): PaddingValues {
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) + paddingValues.calculateStartPadding(
            layoutDirection
        ),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection) + paddingValues.calculateEndPadding(
            layoutDirection
        ),
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}

@Composable
fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    start: Dp = calculateStartPadding(layoutDirection),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(layoutDirection),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start = start, top = top, end = end, bottom = bottom)

fun Modifier.marginPadding(
    dimension: Dimension,
    horizontal: Boolean = false,
    vertical: Boolean = false,
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
) = this then PaddingElement(
    start = if (horizontal || start) dimension.margin else 0.dp,
    top = if (vertical || top) dimension.margin else 0.dp,
    end = if (horizontal || end) dimension.margin else 0.dp,
    bottom = if (vertical || bottom) dimension.margin else 0.dp,
    rtlAware = true,
    inspectorInfo = {
        name = "padding"
        properties["start"] = start
        properties["top"] = top
        properties["end"] = end
        properties["bottom"] = bottom
    }
)

private class PaddingElement(
    var start: Dp = 0.dp,
    var top: Dp = 0.dp,
    var end: Dp = 0.dp,
    var bottom: Dp = 0.dp,
    var rtlAware: Boolean,
    val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<PaddingNode>() {

    init {
        require(
            (start.value >= 0f || start == Dp.Unspecified) &&
                (top.value >= 0f || top == Dp.Unspecified) &&
                (end.value >= 0f || end == Dp.Unspecified) &&
                (bottom.value >= 0f || bottom == Dp.Unspecified)
        ) {
            "Padding must be non-negative"
        }
    }

    override fun create(): PaddingNode {
        return PaddingNode(start, top, end, bottom, rtlAware)
    }

    override fun update(node: PaddingNode) {
        node.start = start
        node.top = top
        node.end = end
        node.bottom = bottom
        node.rtlAware = rtlAware
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + rtlAware.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifierElement = other as? PaddingElement
            ?: return false
        return start == otherModifierElement.start &&
            top == otherModifierElement.top &&
            end == otherModifierElement.end &&
            bottom == otherModifierElement.bottom &&
            rtlAware == otherModifierElement.rtlAware
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }
}

private class PaddingNode(
    var start: Dp = 0.dp,
    var top: Dp = 0.dp,
    var end: Dp = 0.dp,
    var bottom: Dp = 0.dp,
    var rtlAware: Boolean,
) : LayoutModifierNode, Modifier.Node() {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}

@Composable
fun calculatePaddingMargins(contentPadding: PaddingValues): Pair<PaddingValues, PaddingValues> {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    val isCompact =
        windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val paddings = if (isCompact) {
        contentPadding.copy(
            top = 0.dp,
            bottom = 0.dp
        )
    } else {
        contentPadding.copy(top = 0.dp)
    }
    val margins = if (isCompact) {
        PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    } else {
        PaddingValues(
            top = contentPadding.calculateTopPadding(),
        )
    }
    return paddings to margins
}
