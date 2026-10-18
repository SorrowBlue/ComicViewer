/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun FixedWideNavigationRail(
    modifier: Modifier = Modifier,
    state: WideNavigationRailState = rememberWideNavigationRailState(),
    shape: Shape = WideNavigationRailDefaults.shape,
    colors: WideNavigationRailColors = WideNavigationRailDefaults.colors(),
    header: @Composable (() -> Unit)? = null,
    windowInsets: WindowInsets = WideNavigationRailDefaults.windowInsets,
    arrangement: Arrangement.Vertical = WideNavigationRailDefaults.arrangement,
    contentPadding: PaddingValues = WideNavigationRailDefaults.ContentPadding,
    content: @Composable () -> Unit,
) {
    FixedWideNavigationRailLayout(
        isModal = false,
        expanded = state.targetValue == WideNavigationRailValue.Expanded,
        colors = colors,
        shape = shape,
        header = header,
        windowInsets = windowInsets,
        arrangement = arrangement,
        contentPadding = contentPadding,
        modifier = modifier,
        content = content,
    )
}

@Composable
private fun FixedWideNavigationRailLayout(
    isModal: Boolean,
    expanded: Boolean,
    colors: WideNavigationRailColors,
    shape: Shape,
    header: @Composable (() -> Unit)?,
    windowInsets: WindowInsets,
    arrangement: Arrangement.Vertical,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var currentWidth by remember { mutableIntStateOf(0) }
    var actualMaxExpandedWidth by remember { mutableIntStateOf(0) }
    val minimumA11ySize =
        if (LocalMinimumInteractiveComponentSize.current == Dp.Unspecified) {
            0.dp
        } else {
            LocalMinimumInteractiveComponentSize.current
        }

    val animationSpec = ComicTheme.motionScheme.defaultSpatialSpec<Dp>()
    val modalAnimationSpec = ComicTheme.motionScheme.fastSpatialSpec<Dp>()
    val minWidth by animateDpAsState(
        targetValue = if (!expanded) CollapsedRailWidth else ExpandedRailMinWidth,
        animationSpec = if (!isModal) animationSpec else modalAnimationSpec,
        label = "FixedWideNavigationRailMinWidth",
    )
    val widthFullRange by animateDpAsState(
        targetValue = if (!expanded) CollapsedRailWidth else ExpandedRailMaxWidth,
        animationSpec = if (!isModal) animationSpec else modalAnimationSpec,
        label = "FixedWideNavigationRailWidthFullRange",
    )
    val itemVerticalSpacedBy by animateDpAsState(
        targetValue = if (!expanded) ItemVerticalSpace else 0.dp,
        animationSpec = animationSpec,
        label = "FixedWideNavigationRailItemVerticalSpacedBy",
    )
    val itemMinHeight by animateDpAsState(
        targetValue = if (!expanded) TopIconItemMinHeight else minimumA11ySize,
        animationSpec = animationSpec,
        label = "FixedWideNavigationRailItemMinHeight",
    )

    Surface(
        color = if (!isModal) colors.containerColor else colors.modalContainerColor,
        contentColor = if (!isModal) colors.contentColor else colors.modalContentColor,
        shape = shape,
        modifier = modifier,
    ) {
        Layout(
            content = {
                if (header != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(HeaderLayoutIdTag)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        header()
                    }
                }
                content()
            },
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(windowInsets)
                .widthIn(max = ExpandedRailMaxWidth)
                .padding(contentPadding)
                .selectableGroup()
                .semantics { isTraversalGroup = true },
        ) { measurables, constraints ->
            val height = constraints.maxHeight
            val actualMinWidth = if (constraints.minWidth == 0) {
                minWidth.roundToPx().coerceAtMost(constraints.maxWidth)
            } else {
                constraints.minWidth
            }
            val actualExpandedMinWidth = if (constraints.minWidth == 0) {
                ExpandedRailMinWidth.roundToPx().coerceAtMost(constraints.maxWidth)
            } else {
                constraints.minWidth
            }

            val itemsMeasurables = measurables.fastFilter {
                it.layoutId != HeaderLayoutIdTag
            }
            val headerMeasurable = measurables.fastFirstOrNull {
                it.layoutId == HeaderLayoutIdTag
            }

            if (itemsMeasurables.isEmpty() && headerMeasurable == null) {
                return@Layout layout(actualMinWidth, height) {}
            }
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            val headerPlaceable = headerMeasurable?.measure(
                looseConstraints.constrain(
                    Constraints(
                        minWidth = 0,
                        maxWidth = actualMinWidth,
                        minHeight = 0,
                        maxHeight = looseConstraints.maxHeight,
                    ),
                ),
            )

            val headerHeight = headerPlaceable?.height ?: 0
            val headerPaddingPx = if (headerHeight >= WNRHeaderPadding.roundToPx()) {
                WNRHeaderPadding.roundToPx()
            } else {
                headerHeight
            }
            val currentHeaderOffsetPx = headerHeight + headerPaddingPx

            val itemsPlaceables =
                if (itemsMeasurables.isNotEmpty()) mutableListOf<Placeable>() else null
            val itemMaxWidthConstraint =
                (if (expanded) looseConstraints.maxWidth else actualMinWidth)
                    .coerceAtLeast(minimumA11ySize.roundToPx())
            val itemMaxHeightConstraint =
                looseConstraints.maxHeight.coerceAtLeast(itemMinHeight.roundToPx())
            var expandedItemMaxWidth = 0
            var cumulativeOffset = currentHeaderOffsetPx
            if (itemsPlaceables != null) {
                itemsMeasurables.fastMap {
                    val measuredItem = it.measure(
                        looseConstraints
                            .offset(vertical = -cumulativeOffset)
                            .constrain(
                                Constraints(
                                    minWidth = minimumA11ySize.roundToPx(),
                                    minHeight = itemMinHeight.roundToPx(),
                                    maxWidth = itemMaxWidthConstraint,
                                    maxHeight = itemMaxHeightConstraint,
                                ),
                            ),
                    )
                    val maxItemWidth = measuredItem.measuredWidth
                    if (expanded && expandedItemMaxWidth < maxItemWidth) {
                        expandedItemMaxWidth = maxItemWidth + ItemHorizontalPadding.roundToPx()
                    }
                    cumulativeOffset += measuredItem.height
                    itemsPlaceables.add(measuredItem)
                }
            }

            var width = actualMinWidth
            if (expanded) {
                val widestElementWidth = maxOf(
                    expandedItemMaxWidth,
                    headerPlaceable?.width ?: 0,
                )
                if (widestElementWidth > actualMinWidth &&
                    widestElementWidth > actualExpandedMinWidth
                ) {
                    val widthConstrain = maxOf(widestElementWidth, actualExpandedMinWidth)
                        .coerceAtMost(constraints.maxWidth)
                    width = widthFullRange.roundToPx().coerceAtMost(widthConstrain)
                    actualMaxExpandedWidth = width
                }
            } else {
                if (actualMaxExpandedWidth > 0) {
                    width = widthFullRange
                        .roundToPx()
                        .coerceIn(
                            minimumValue = actualMinWidth,
                            maximumValue = currentWidth.coerceAtLeast(actualMinWidth),
                        )
                }
            }
            currentWidth = width

            layout(width, height) {
                if (headerPlaceable != null && headerPlaceable.height > 0) {
                    headerPlaceable.placeRelative(0, 0)
                }

                if (itemsPlaceables != null) {
                    val layoutSize = if (arrangement == Arrangement.Center) {
                        height
                    } else {
                        height - currentHeaderOffsetPx
                    }
                    val sizes = IntArray(itemsPlaceables.size)
                    itemsPlaceables.fastForEachIndexed { index, item ->
                        sizes[index] = item.height
                        if (index < itemsPlaceables.size - 1) {
                            sizes[index] += itemVerticalSpacedBy.roundToPx()
                        }
                    }
                    val y = IntArray(itemsPlaceables.size)
                    with(arrangement) { arrange(layoutSize, sizes, y) }

                    val offset = if (arrangement ==
                        Arrangement.Center
                    ) {
                        0
                    } else {
                        currentHeaderOffsetPx
                    }
                    itemsPlaceables.fastForEachIndexed { index, item ->
                        item.placeRelative(0, y[index] + offset)
                    }
                }
            }
        }
    }
}

private val ItemHorizontalPadding = 20.dp
private val WNRHeaderPadding = 40.dp
private val CollapsedRailWidth = 96.dp
private val ExpandedRailMinWidth = 220.dp
private val ExpandedRailMaxWidth = 360.dp
private val TopIconItemMinHeight = 64.dp
private val ItemVerticalSpace = 4.dp
private const val HeaderLayoutIdTag: String = "header"
