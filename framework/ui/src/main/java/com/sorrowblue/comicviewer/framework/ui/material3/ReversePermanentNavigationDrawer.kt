package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import kotlinx.collections.immutable.toPersistentList

@Composable
fun ReversePermanentNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection.reverse()) {
        PermanentNavigationDrawer(
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    drawerContent()
                }
            },
            modifier
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    CompositionLocalProvider(
        LocalWindowSize provides WindowSizeClass.calculateFromSize(
            DpSize(
                configuration.screenWidthDp.dp,
                configuration.screenHeightDp.dp
            )
        )
    ) {
        ComicTheme(useDynamicColor = true) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun PreviewScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val items = remember {
        listOf("Home", "Book", "Folder", "View", "Settings").toPersistentList()
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach {
                item(selected = false, onClick = {}, icon = {
                    Icon(
                        imageVector = ComicIcons.Image,
                        contentDescription = null
                    )
                }, label = {
                    Text(text = it)
                })
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = ComicTheme.colorScheme.surfaceContainer,
            navigationRailContainerColor = ComicTheme.colorScheme.surfaceContainer,
            navigationDrawerContainerColor = ComicTheme.colorScheme.surfaceContainer
        ),
        layoutType = if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
            NavigationSuiteType.NavigationBar
        } else {
            NavigationSuiteType.NavigationRail
        },
        modifier = modifier,
    ) {
        content()
    }
}

@PreviewComic
@Composable
private fun PreviewPreviewScreen() {
    PreviewTheme {
        PreviewScreen {
            Column(modifier = Modifier.fillMaxSize()) {
            }
        }
    }
}

private fun LayoutDirection.reverse() = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}
