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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.material3.AppBarAction
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import kotlinx.collections.immutable.PersistentList

object ResponsiveTopAppBarDefault {
    @OptIn(ExperimentalMaterial3Api::class)
    val scrollBehavior @Composable get() = if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) TopAppBarDefaults.pinnedScrollBehavior() else null
}

@Composable
fun ResponsiveTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
    scrollBehavior: com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior? = null,
) {
    if (rememberMobile()) {
        com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior
        )
    } else {
        com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar(
            title = title,
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets(0),
            modifier = modifier
                .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                .padding(horizontal = ComicTheme.dimension.margin)
                .clip(ComicTheme.shapes.large)
        )
    }
}

@Composable
fun ResponsiveTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
    scrollBehavior: com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior? = null,
) {
    ResponsiveTopAppBar(
        title = {
            Text(text = title)
        },
        modifier = modifier,
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ResponsiveTopAppBar(
    title: Int,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
    scrollBehavior: com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior? = null,
) {
    ResponsiveTopAppBar(
        title = stringResource(id = title),
        modifier = modifier,
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T : AppBarAction> ResponsiveTopAppBar(
    onClick: (T) -> Unit,
    actions: PersistentList<T>,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
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
                OverflowMenu(state = overflowMenuState) {
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

interface AppBarAction2 {
    val label: Int
    val description: Int
    val icon: ImageVector
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T : AppBarAction2> ResponsiveTopAppBar2(
    actions: PersistentList<T>,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            if (actions.size <= 3) {
                actions.forEach { action ->
                    key(action.label) {
                        PlainTooltipBox(tooltipContent = {
                            Text(text = stringResource(id = action.label))
                        }) {
                            IconButton(onClick = { onClick.invoke(action) }) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = stringResource(id = action.description)
                                )
                            }
                        }
                    }
                }
            } else {
                actions.take(2).forEach { action ->
                    key(action.label) {
                        PlainTooltipBox(tooltipContent = { Text(text = stringResource(id = action.label)) }) {
                            IconButton(onClick = { onClick.invoke(action) }) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = stringResource(id = action.description)
                                )
                            }
                        }
                    }
                }
                val overflowMenuState = rememberOverflowMenuState()
                OverflowMenu(state = overflowMenuState) {
                    actions.drop(2).forEach { action ->
                        key(action.label) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = action.label)) },
                                trailingIcon = {
                                    Icon(
                                        action.icon,
                                        stringResource(id = action.description)
                                    )
                                },
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
fun <T : AppBarAction2> ResponsiveTopAppBar2(
    actions: PersistentList<T>,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    scrollBehavior: TopAppBarScrollBehavior? = ResponsiveTopAppBarDefault.scrollBehavior,
) {
    ResponsiveTopAppBar2(
        actions = actions,
        onClick = onClick,
        modifier = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            modifier
        } else {
            modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.End))
                .padding(horizontal = ComicTheme.dimension.margin)
                .padding(end = ComicTheme.dimension.margin)
                .clip(CircleShape)
        },
        title = title,
        navigationIcon = navigationIcon,
        scrollBehavior = scrollBehavior
    )
}
