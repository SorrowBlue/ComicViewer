package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

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
        ComicTheme {
            content()
        }
    }
}

@Composable
fun SideSheetScaffold(visibleSideSheet: Boolean) {
    Surface(color = ComicTheme.colorScheme.surfaceVariant) {
        val alpha by animateFloatAsState(
            targetValue = if (visibleSideSheet) 1f else 0f,
            label = "alpha"
        )
        ReversePermanentNavigationDrawer(
            modifier = Modifier
                .fillMaxSize()
                .padding(ComicTheme.dimension.margin),
            drawerContent = {
                AnimatedContent(
                    targetState = visibleSideSheet,
                    label = "side_sheet",
                    transitionSpec = {
                        expandHorizontally(expandFrom = Alignment.End) { it } togetherWith shrinkHorizontally(
                            shrinkTowards = Alignment.Start
                        ) { it }
                    },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (it) {
                        Surface(
                            modifier = Modifier
                                .width(256.dp)
                                .padding(start = ComicTheme.dimension.spacer)
                                .clip(RoundedCornerShape(16.dp)),
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides contentColorFor(ComicTheme.colorScheme.surface).copy(
                                    alpha = alpha
                                )
                            ) {
                                LazyColumn {
                                    items(20, key = { it }) {
                                        ListItem(
                                            headlineContent = { Text("Label: $it") },
                                            leadingContent = {
                                                Icon(
                                                    imageVector = ComicIcons.Image,
                                                    contentDescription = null
                                                )
                                            },
                                            colors = ListItemDefaults.colors(
                                                headlineColor = LocalContentColor.current
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.fillMaxHeight())
                    }
                }
            }
        ) {
            LazyColumn(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(ComicTheme.colorScheme.surface)
            ) {
                items(20, key = { it }) {
                    NavigationDrawerItem(
                        label = { Text("Content: $it") },
                        selected = false,
                        onClick = { })
                }
            }
        }
    }
}


@Composable
fun PermanentNavigationDrawer2(
    visibleSideSheet: Boolean,
    sideSheet: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    ComicTheme {
        Surface(color = ComicTheme.colorScheme.surfaceVariant) {
            val alpha by animateFloatAsState(
                targetValue = if (visibleSideSheet) 1f else 0f,
                label = "alpha"
            )
            ReversePermanentNavigationDrawer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ComicTheme.dimension.margin),
                drawerContent = {
                    AnimatedContent(
                        targetState = visibleSideSheet,
                        label = "side_sheet",
                        transitionSpec = {
                            expandHorizontally(expandFrom = Alignment.End) { it } togetherWith shrinkHorizontally(
                                shrinkTowards = Alignment.Start
                            ) { it }
                        },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (it) {
                            Surface(
                                modifier = Modifier
                                    .width(400.dp)
                                    .padding(start = ComicTheme.dimension.spacer)
                                    .clip(RoundedCornerShape(16.dp)),
                            ) {
                                CompositionLocalProvider(
                                    LocalContentColor provides contentColorFor(ComicTheme.colorScheme.surface).copy(
                                        alpha = alpha
                                    )
                                ) {
                                    sideSheet()
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxHeight())
                        }
                    }
                }
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ComicTheme.colorScheme.surface)
                ) {
                    content()
                }
            }
        }
    }
}


private fun LayoutDirection.reverse() = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}
