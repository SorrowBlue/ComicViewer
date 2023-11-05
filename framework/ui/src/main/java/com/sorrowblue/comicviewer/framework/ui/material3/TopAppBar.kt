package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.pinnedScrollBehavior(),
    windowInsets: WindowInsets? = null,
) {
    if (rememberMobile()) {
        androidx.compose.material3.TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior?.value,
            modifier = modifier,
            windowInsets = windowInsets ?: androidx.compose.material3.TopAppBarDefaults.windowInsets
        )
    } else {
        androidx.compose.material3.TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior?.value,
            windowInsets = windowInsets
                ?: WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.pinnedScrollBehavior(),
    windowInsets: WindowInsets? = null,
) {
    androidx.compose.material3.TopAppBar(
        title = title,
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior?.value,
        modifier = modifier,
        windowInsets = windowInsets ?: androidx.compose.material3.TopAppBarDefaults.windowInsets
    )
}

@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets? = null,
) {
    TopAppBar(
        title = { Text(text = title) },
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun TopAppBar(
    title: Int,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets? = null,
) {
    TopAppBar(
        title = stringResource(id = title),
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@JvmInline
value class TopAppBarScrollBehavior(val value: androidx.compose.material3.TopAppBarScrollBehavior) {

    val nestedScrollConnection get() = value.nestedScrollConnection
}

object TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDefaults.pinnedScrollBehavior(): TopAppBarScrollBehavior {
    return TopAppBarScrollBehavior(androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior())
}
