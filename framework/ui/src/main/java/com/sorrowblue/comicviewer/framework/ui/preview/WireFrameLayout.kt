package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.responsive.LocalVisibleNavigationRail
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveLayout
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveLayoutState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WireFrameLayout(
    state: ResponsiveLayoutState,
    onClickItem: (Int) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    PreviewTheme {
        val windowSizeClass = LocalWindowSize.current
        val size = windowSizeClass.widthSizeClass
        val isCompact = size == WindowWidthSizeClass.Compact
        val scope = rememberCoroutineScope()
        fun toggle() {
            if (state.navigationState.currentValue) {
                state.navigationState.hide()
            } else {
                state.navigationState.show()
            }
        }
        ResponsiveLayout(
            state = state,
            navigationRail = {
                NavigationRail(
                    header = {
                        FloatingActionButton(
                            onClick = {
                                toggle()
                                scope.launch {
                                    delay(4000)
                                    toggle()
                                }
                            },
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(imageVector = ComicIcons.Add, contentDescription = null)
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
                    containerColor = ComicTheme.colorScheme.surfaceContainer
                ) {
                    repeat(4) {
                        NavigationRailItem(
                            selected = false,
                            onClick = { onClickItem(it) },
                            icon = {
                                Icon(
                                    imageVector = ComicIcons.Book,
                                    contentDescription = null
                                )
                            })
                    }
                }
            },
            navigationBar = {
                NavigationBar(containerColor = ComicTheme.colorScheme.surfaceContainer) {
                    repeat(4) {
                        NavigationBarItem(
                            selected = false,
                            onClick = { onClickItem(it) },
                            icon = {
                                Icon(
                                    imageVector = ComicIcons.Book,
                                    contentDescription = null
                                )
                            })
                    }
                }
            },
            floatingActionButton = {
                if (isCompact) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Add Foo") },
                        icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                        onClick = {
                            toggle()
                            scope.launch {
                                delay(4000)
                                toggle()
                            }
                        }
                    )
                }
            },
            content = {
                CompositionLocalProvider(LocalVisibleNavigationRail provides state.navigationState.currentValue) {
                    content(it)
                }
            }
        )
    }
}
