package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.material3.AppBarAction
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import kotlinx.collections.immutable.PersistentList

object ResponsiveTopAppBarDefault {
    @OptIn(ExperimentalMaterial3Api::class)
    val scrollBehavior @Composable get() = if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) TopAppBarDefaults.pinnedScrollBehavior() else null
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ResponsiveTopAppBar(
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.End))
                .padding(horizontal = ComicTheme.dimension.margin)
                .padding(end = ComicTheme.dimension.margin)
                .clip(CircleShape),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T : AppBarAction> ResponsiveTopAppBar(
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: PersistentList<T>,
    onClick: (T) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
    modifier: Modifier,
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            if (actions.size <= 3) {
                actions.forEach { action ->
                    key(action.label) {
                        PlainTooltipBox(tooltipContent = {
                            Text(text = action.label)
                        }) {
                            IconButton(onClick = { onClick.invoke(action) }) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.label
                                )
                            }
                        }
                    }
                }
            } else {
                actions.take(2).forEach { action ->
                    key(action.label) {
                        PlainTooltipBox(tooltipContent = { Text(text = action.label) }) {
                            IconButton(onClick = { onClick.invoke(action) }) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.label
                                )
                            }
                        }
                    }
                }
                val overflowMenuState = rememberOverflowMenuState()
                OverflowMenu(overflowMenuState) {
                    actions.drop(2).forEach { action ->
                        key(action.label) {
                            DropdownMenuItem(
                                text = { Text(action.label) },
                                trailingIcon = { Icon(action.icon, action.label) },
                                onClick = {
                                    overflowMenuState.collapse()
                                    onClick.invoke(action)
                                }
                            )
                        }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T : AppBarAction> ResponsiveTopAppBar(
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: PersistentList<T>,
    onClick: (T) -> Unit,
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
) {
    val modifier = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        Modifier
    } else {
        Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.End))
            .padding(horizontal = ComicTheme.dimension.margin)
            .padding(end = ComicTheme.dimension.margin)
            .clip(CircleShape)
    }
    ResponsiveTopAppBar(title, navigationIcon, actions, onClick, scrollBehavior, modifier)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FullScreenTopAppBar(
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(horizontal = ComicTheme.dimension.margin * 2)
                .clip(CircleShape),
        )
    }
}
