package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
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
//    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    if (rememberMobile()) {
        androidx.compose.material3.TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets
                ?: androidx.compose.material3.TopAppBarDefaults.windowInsets,
            colors = TopAppBarDefaults.topAppBarColors().value,
            scrollBehavior = scrollBehavior?.value
        )
    } else {
        androidx.compose.material3.TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets
                ?: WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            colors = TopAppBarDefaults.topAppBarColors().value,
            scrollBehavior = scrollBehavior?.value
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
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    androidx.compose.material3.TopAppBar(
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
        windowInsets = windowInsets ?: androidx.compose.material3.TopAppBarDefaults.windowInsets,
        colors = colors.value,
        scrollBehavior = scrollBehavior?.value
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
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
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
        modifier = modifier,
        onBackClick = onBackClick,
        actions = actions,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@JvmInline
value class TopAppBarScrollBehavior(val value: androidx.compose.material3.TopAppBarScrollBehavior) {

    val nestedScrollConnection get() = value.nestedScrollConnection
}

@OptIn(ExperimentalMaterial3Api::class)
@JvmInline
value class TopAppBarColors(val value: androidx.compose.material3.TopAppBarColors)

object TopAppBarDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBarColors(
        containerColor: Color = ComicTheme.colorScheme.surface,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.applyTonalElevation(
            backgroundColor = containerColor,
            elevation = ElevationTokens.Level2
        ),
        navigationIconContentColor: Color = ComicTheme.colorScheme.onSurface,
        titleContentColor: Color = ComicTheme.colorScheme.onSurface,
        actionIconContentColor: Color = ComicTheme.colorScheme.onSurfaceVariant,
    ): TopAppBarColors = TopAppBarColors(
        androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )
    )
}

@Composable
@ReadOnlyComposable
internal fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    val tonalElevationEnabled = LocalTonalElevationEnabled.current
    return if (backgroundColor == surface && tonalElevationEnabled) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDefaults.pinnedScrollBehavior(): TopAppBarScrollBehavior {
    return TopAppBarScrollBehavior(androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior())
}
