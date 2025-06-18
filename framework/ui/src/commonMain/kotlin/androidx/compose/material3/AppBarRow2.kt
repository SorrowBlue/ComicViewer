package androidx.compose.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxOfOrNull
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlin.math.max

@Composable
@Suppress("ComposableLambdaParameterPosition", "KotlinDefaultParameterOrder")
fun AppBarRow2(
    modifier: Modifier = Modifier,
    overflowIndicator: @Composable (AppBarMenuState2) -> Unit = {
        IconButton(onClick = it::show) {
            Icon(ComicIcons.MoreVert, null)
        }
    },
    maxItemCount: Int = 3,
    content: AppBarRowScope2.() -> Unit,
) {
    val latestContent = rememberUpdatedState(content)
    val scope by remember {
        derivedStateOf { AppBarRowScope2Impl(AppBarScope2Impl()).apply(latestContent.value) }
    }
    val menuState = remember { AppBarMenuState2() }
    val overflowState = rememberAppBarOverflowState2()
    val measurePolicy =
        remember(overflowState, maxItemCount) {
            OverflowMeasurePolicy2(overflowState, maxItemCount, isVertical = false)
        }

    Layout(
        contents =
        listOf(
            { scope.items.fastForEach { it.AppbarContent() } },
            {
                Box {
                    overflowIndicator(menuState)
                    DropdownMenu(
                        expanded = menuState.isExpanded,
                        onDismissRequest = { menuState.dismiss() }
                    ) {
                        scope.items
                            .subList(
                                overflowState.visibleItemCount,
                                overflowState.totalItemCount
                            )
                            .fastForEach { item -> item.MenuContent(menuState) }
                    }
                }
            }
        ),
        modifier = modifier,
        measurePolicy = measurePolicy,
    )
}

/** DSL scope for building the content of an [AppBarRow]. */
interface AppBarRowScope2 : AppBarScope2

private class AppBarRowScope2Impl(val impl: AppBarScope2Impl) :
    AppBarRowScope2, AppBarScope2 by impl, AppBarItemProvider2 by impl

sealed interface AppBarScope2 {

    /**
     * Adds a clickable item to the [AppBarRow] or [AppBarColumn].
     *
     * @param onClick The action to perform when the item is clicked.
     * @param icon The composable representing the item's icon.
     * @param label The text label for the item, used in the overflow menu.
     * @param enabled Whether the item is enabled.
     */
    fun clickableItem(
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit,
        enabled: Boolean = true,
        visible: Boolean = true,
        autoDismiss: Boolean = true,
    )

    /**
     * Adds a toggleable item to the [AppBarRow] or [AppBarColumn].
     *
     * @param checked Whether the item is currently checked.
     * @param onCheckedChange The action to perform when the item's checked
     *    state changes.
     * @param icon The composable representing the item's icon.
     * @param label The text label for the item, used in the overflow menu.
     * @param enabled Whether the item is enabled.
     */
    fun toggleableItem(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit,
        enabled: Boolean = true,
        visible: Boolean = true,
        autoDismiss: Boolean = true,
    )

    /**
     * Adds a custom item to the [AppBarRow] or [AppBarColumn].
     *
     * @param appbarContent The composable to display in the app bar.
     * @param menuContent The composable to display in the overflow menu. It
     *    receives an [AppBarMenuState2] instance.
     */
    fun customItem(
        appbarContent: @Composable () -> Unit,
        menuContent: @Composable (AppBarMenuState2) -> Unit,
    )
}

internal interface AppBarItemProvider2 {
    val itemsCount: Int
    val items: MutableList<AppBarItem2>
}

internal interface AppBarItem2 {

    /** Composable function to render the item in the app bar. */
    @Composable
    fun AppbarContent()

    /**
     * Composable function to render the item in the overflow menu.
     *
     * @param state The [AppBarMenuState2] instance.
     */
    @Composable
    fun MenuContent(state: AppBarMenuState2)
}

internal class AppBarScope2Impl : AppBarScope2, AppBarItemProvider2 {

    override val items = mutableListOf<AppBarItem2>()

    override val itemsCount: Int
        get() = items.size

    override fun clickableItem(
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit,
        enabled: Boolean,
        visible: Boolean,
        autoDismiss: Boolean,
    ) {
        items.add(
            ClickableAppBarItem2(
                onClick = onClick,
                icon = icon,
                enabled = enabled,
                visible = visible,
                autoDismiss = autoDismiss,
                label = label
            )
        )
    }

    override fun toggleableItem(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit,
        enabled: Boolean,
        visible: Boolean,
        autoDismiss: Boolean,
    ) {
        items.add(
            ToggleableAppBarItem2(
                checked = checked,
                onCheckedChange = onCheckedChange,
                icon = icon,
                enabled = enabled,
                visible = visible,
                autoDismiss = autoDismiss,
                label = label
            )
        )
    }

    override fun customItem(
        appbarContent: @Composable () -> Unit,
        menuContent: @Composable (AppBarMenuState2) -> Unit,
    ) {
        items.add(CustomAppBarItem2(appbarContent, menuContent))
    }
}

internal class ClickableAppBarItem2(
    private val onClick: () -> Unit,
    private val icon: @Composable () -> Unit,
    private val visible: Boolean,
    private val enabled: Boolean,
    private val autoDismiss: Boolean,
    private val label: @Composable () -> Unit,
) : AppBarItem2 {

    @Composable
    override fun AppbarContent() {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            content = icon,
        )
    }

    @Composable
    override fun MenuContent(state: AppBarMenuState2) {
        if (visible) {
            DropdownMenuItem(
                leadingIcon = icon,
                enabled = enabled,
                text = label,
                onClick = {
                    onClick()
                    if (autoDismiss) {
                        state.dismiss()
                    }
                }
            )
        }
    }
}

internal class ToggleableAppBarItem2(
    private val checked: Boolean,
    private val onCheckedChange: (Boolean) -> Unit,
    private val icon: @Composable () -> Unit,
    private val enabled: Boolean,
    private val visible: Boolean,
    private val autoDismiss: Boolean,
    private val label: @Composable () -> Unit,
) : AppBarItem2 {

    @Composable
    override fun AppbarContent() {
        IconToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            content = icon,
        )
    }

    @Composable
    override fun MenuContent(state: AppBarMenuState2) {
        if (visible) {
            DropdownMenuItem(
                leadingIcon = icon,
                trailingIcon = {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        enabled = enabled
                    )
                },
                enabled = enabled,
                text = label,
                onClick = {
                    onCheckedChange(!checked)
                    if (autoDismiss) {
                        state.dismiss()
                    }
                }
            )
        }
    }
}

internal class CustomAppBarItem2(
    private val appbarContent: @Composable () -> Unit,
    private val menuContent: @Composable (AppBarMenuState2) -> Unit,
) : AppBarItem2 {
    @Composable
    override fun AppbarContent() {
        appbarContent()
    }

    @Composable
    override fun MenuContent(state: AppBarMenuState2) {
        menuContent(state)
    }
}

/** State class for the overflow menu in [AppBarRow] and [AppBarColumn]. */
class AppBarMenuState2 {

    /** Indicates whether the overflow menu is currently expanded. */
    var isExpanded by mutableStateOf(false)
        private set

    /** Closes the overflow menu. */
    fun dismiss() {
        isExpanded = false
    }

    /** Show the overflow menu. */
    fun show() {
        isExpanded = true
    }
}

internal interface AppBarOverflowState2 {

    var totalItemCount: Int

    var visibleItemCount: Int
}

@Composable
internal fun rememberAppBarOverflowState2(): AppBarOverflowState2 {
    return rememberSaveable(saver = AppBarOverflowState2Impl.Saver) { AppBarOverflowState2Impl() }
}

private class AppBarOverflowState2Impl : AppBarOverflowState2 {
    override var totalItemCount: Int by mutableIntStateOf(0)
    override var visibleItemCount: Int by mutableIntStateOf(0)

    companion object {
        val Saver: Saver<AppBarOverflowState2Impl, *> =
            Saver(
                save = { listOf(it.totalItemCount, it.visibleItemCount) },
                restore = {
                    AppBarOverflowState2Impl().apply {
                        totalItemCount = it[0]
                        visibleItemCount = it[1]
                    }
                }
            )
    }
}

internal class OverflowMeasurePolicy2(
    private val overflowState: AppBarOverflowState2,
    val maxItemCount: Int,
    private val isVertical: Boolean = false,
) : MultiContentMeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints,
    ): MeasureResult {
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val (contentMeasurables, overflowMeasurables) = measurables

        overflowState.totalItemCount = contentMeasurables.size

        // First, reserve space for the overflow indicator
        val reservedOverflowSpace =
            if (isVertical) {
                overflowMeasurables.fastMaxOfOrNull { it.maxIntrinsicHeight(constraints.maxWidth) }
                    ?: 0
            } else {
                overflowMeasurables.fastMaxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) }
                    ?: 0
            }
        var remainingSpace = if (isVertical) constraints.maxHeight else constraints.maxWidth
        remainingSpace = remainingSpace.subtractConstraintSafely2(reservedOverflowSpace)

        var currentSpace = 0
        val contentPlaceables = mutableListOf<Placeable>()

        // Measure content until it doesn't fit
        @Suppress("LoopWithTooManyJumpStatements")
        for (i in contentMeasurables.indices) {
            val placeable = contentMeasurables[i].measure(looseConstraints)
            val isLastContent = i == contentMeasurables.lastIndex
            if (!isLastContent && (i == maxItemCount - 1)) {
                break
            }
            val placeableSpace = if (isVertical) placeable.height else placeable.width
            val hasSufficientSpace =
                placeableSpace <= remainingSpace ||
                    (isLastContent && placeableSpace <= remainingSpace + reservedOverflowSpace)

            if (hasSufficientSpace) {
                contentPlaceables.add(placeable)
                currentSpace += placeableSpace
                remainingSpace = remainingSpace.subtractConstraintSafely2(placeableSpace)
            } else {
                break
            }
        }

        overflowState.visibleItemCount = contentPlaceables.size

        // Measure overflow if needed
        val overflowPlaceables =
            if (contentPlaceables.size != contentMeasurables.size) {
                val overflowConstraints =
                    if (isVertical) {
                        looseConstraints.copy(maxHeight = remainingSpace + reservedOverflowSpace)
                    } else {
                        looseConstraints.copy(maxWidth = remainingSpace + reservedOverflowSpace)
                    }
                overflowMeasurables.fastMap { it.measure(overflowConstraints) }
            } else {
                null
            }

        val overflowSpace =
            if (isVertical) {
                overflowPlaceables?.fastMaxOfOrNull { it.height } ?: 0
            } else {
                overflowPlaceables?.fastMaxOfOrNull { it.width } ?: 0
            }
        currentSpace += overflowSpace

        val childrenMaxSpace =
            if (isVertical) {
                max(
                    contentPlaceables.fastMaxOfOrNull { it.width } ?: 0,
                    overflowPlaceables?.fastMaxOfOrNull { it.width } ?: 0,
                )
            } else {
                max(
                    contentPlaceables.fastMaxOfOrNull { it.height } ?: 0,
                    overflowPlaceables?.fastMaxOfOrNull { it.height } ?: 0,
                )
            }

        var width: Int
        var height: Int
        return if (isVertical) {
            width = constraints.constrainWidth(childrenMaxSpace)
            height = constraints.constrainHeight(currentSpace)

            layout(width, height) {
                var currentY = 0
                contentPlaceables.fastForEach {
                    it.placeRelative(x = 0, y = currentY)
                    currentY += it.height
                }
                overflowPlaceables?.fastForEach { it.placeRelative(x = 0, y = currentY) }
            }
        } else {
            width = constraints.constrainWidth(currentSpace)
            height = constraints.constrainHeight(childrenMaxSpace)

            layout(width, height) {
                var currentX = 0
                contentPlaceables.fastForEach {
                    it.placeRelative(x = currentX, y = 0)
                    currentX += it.width
                }
                overflowPlaceables?.fastForEach { it.placeRelative(x = currentX, y = 0) }
            }
        }
    }
}

internal fun Int.subtractConstraintSafely2(other: Int): Int {
    if (this == Constraints.Infinity) {
        return this
    }
    return (this - other).coerceAtLeast(0)
}
