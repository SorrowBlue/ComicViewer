package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.ShortNavigationBarArrangement
import androidx.compose.material3.ShortNavigationBarOverride
import androidx.compose.material3.ShortNavigationBarOverrideScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import kotlin.math.roundToInt

/**
 * Fixed [ androidx.compose.material3.EqualWeightContentMeasurePolicy] and [ androidx.compose.material3.CenteredContentMeasurePolicy]
 *
 * @see androidx.compose.material3.DefaultShortNavigationBarOverride
 */
@ExperimentalMaterial3ComponentOverrideApi
object FixedDefaultShortNavigationBarOverride : ShortNavigationBarOverride {
    @Composable
    override fun ShortNavigationBarOverrideScope.ShortNavigationBar() {
        Surface(color = containerColor, contentColor = contentColor, modifier = modifier) {
            Layout(
                modifier =
                    Modifier
                        .windowInsetsPadding(windowInsets)
                        .defaultMinSize(minHeight = ContainerHeight)
                        .selectableGroup(),
                content = content,
                measurePolicy =
                    when (arrangement) {
                        ShortNavigationBarArrangement.EqualWeight -> {
                            FixedEqualWeightContentMeasurePolicy()
                        }

                        ShortNavigationBarArrangement.Centered -> {
                            FixedCenteredContentMeasurePolicy()
                        }

                        else -> {
                            throw IllegalArgumentException("Invalid ItemsArrangement value.")
                        }
                    },
            )
        }
    }
}

/**
 * Correct 0 in minWidth because it doesn't animation correctly in SharedElement
 *
 * @see androidx.compose.material3.EqualWeightContentMeasurePolicy
 */
private class FixedEqualWeightContentMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val width = constraints.maxWidth
        var itemHeight = constraints.minHeight
        val itemsCount = measurables.size
        // If there are no items, bar will be empty.
        if (itemsCount < 1) {
            return layout(width, itemHeight) {}
        }

        val itemsPlaceables: List<Placeable>
        if (!constraints.hasBoundedWidth) {
            // If width constraint is not bounded, let item containers widths be as big as they are.
            // This may lead to a different items arrangement than the expected.
            itemsPlaceables =
                measurables.fastMap {
                    it.measure(constraints.constrain(Constraints.fixedHeight(height = itemHeight)))
                }
        } else {
            val itemWidth = width / itemsCount
            measurables.fastForEach {
                val measurableHeight = it.maxIntrinsicHeight(itemWidth)
                if (itemHeight < measurableHeight) {
                    itemHeight = measurableHeight.coerceAtMost(constraints.maxHeight)
                }
            }

            // Make sure the item containers have the same width and height.
            itemsPlaceables =
                measurables.fastMap {
                    // TODO Correct 0 in minWidth because it doesn't animation correctly in SharedElement
                    val constraintsFixed = constraints.copy(minWidth = 0)

                    it.measure(
                        constraintsFixed.constrain(
                            Constraints.fixed(width = itemWidth, height = itemHeight),
                        ),
                    )
                }
        }

        return layout(width, itemHeight) {
            var x = 0
            val y = 0
            itemsPlaceables.fastForEach { item ->
                item.placeRelative(x, y)
                x += item.width
            }
        }
    }
}

/**
 * Correct 0 in minWidth because it doesn't animation correctly in SharedElement
 *
 * @see androidx.compose.material3.CenteredContentMeasurePolicy
 */
private class FixedCenteredContentMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val width = constraints.maxWidth
        var itemHeight = constraints.minHeight
        val itemsCount = measurables.size
        // If there are no items, bar will be empty.
        if (itemsCount < 1) {
            return layout(width, itemHeight) {}
        }

        var barHorizontalPadding = 0
        val itemsPlaceables: List<Placeable>
        if (!constraints.hasBoundedWidth) {
            // If width constraint is not bounded, let item containers widths be as big as they are.
            // This may lead to a different items arrangement than the expected.
            itemsPlaceables =
                measurables.fastMap {
                    it.measure(constraints.constrain(Constraints.fixedHeight(height = itemHeight)))
                }
        } else {
            val itemMaxWidth = width / itemsCount
            barHorizontalPadding = calculateCenteredContentHorizontalPadding(itemsCount, width)
            val itemMinWidth = (width - (barHorizontalPadding * 2)) / itemsCount

            // Make sure the item containers will have the same height.
            measurables.fastForEach {
                val measurableHeight = it.maxIntrinsicHeight(itemMinWidth)
                if (itemHeight < measurableHeight) {
                    itemHeight = measurableHeight.coerceAtMost(constraints.maxHeight)
                }
            }
            itemsPlaceables =
                measurables.fastMap {
                    var currentItemWidth = itemMinWidth
                    val measurableWidth = it.maxIntrinsicWidth(constraints.minHeight)
                    if (currentItemWidth < measurableWidth) {
                        // Let an item container be bigger in width if needed, but limit it to
                        // itemMaxWidth.
                        currentItemWidth = measurableWidth.coerceAtMost(itemMaxWidth)
                        // Update horizontal padding so that items remain centered.
                        barHorizontalPadding -= (currentItemWidth - itemMinWidth) / 2
                    }

                    // TODO Correct 0 in minWidth because it doesn't animation correctly in SharedElement
                    val constraintsFixed = constraints.copy(minWidth = 0)

                    it.measure(
                        constraintsFixed.constrain(
                            Constraints.fixed(width = currentItemWidth, height = itemHeight),
                        ),
                    )
                }
        }

        return layout(width, itemHeight) {
            var x = barHorizontalPadding
            val y = 0
            itemsPlaceables.fastForEach { item ->
                item.placeRelative(x, y)
                x += item.width
            }
        }
    }
}

/**
 * @see androidx.compose.material3.calculateCenteredContentHorizontalPadding
 */
private fun calculateCenteredContentHorizontalPadding(itemsCount: Int, barWidth: Int): Int {
    if (itemsCount > MaxItemCount) return 0
    // Formula to calculate the padding percentage based on the number of items and bar width.
    val paddingPercentage = ((100 - 10 * (itemsCount + 3)) / 2f) / 100
    return (paddingPercentage * barWidth).roundToInt()
}

/**
 * @see androidx.compose.material3.tokens.NavigationBarTokens.ContainerHeight
 */
private val ContainerHeight = 64.0.dp

private const val MaxItemCount = 6
