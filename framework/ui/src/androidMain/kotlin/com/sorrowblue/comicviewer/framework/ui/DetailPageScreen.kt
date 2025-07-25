package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigableExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.LocalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.layout.animatePaddingValues
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.layout.union
import kotlinx.coroutines.launch

@Composable
fun DetailPageScreen() {
    val scaffoldState = LocalScaffoldState.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scaffoldState.navigationSuiteScaffoldState.hide()
    }
    val navigator = rememberSupportingPaneScaffoldNavigator<String>()
    NavigableExtraPaneScaffold(
        navigator = navigator,
        extraPane = {
            val destination = navigator.currentDestination
            if (destination?.pane == SupportingPaneScaffoldRole.Extra && destination.contentKey != null) {
                Scaffold(
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Text("Detail")
                                },
                                actions = {
                                    IconButton(onClick = { scope.launch { navigator.navigateBack() } }
                                    ) {
                                        Icon(ComicIcons.Close, null)
                                    }
                                },
                            )
                            HorizontalDivider()
                        }
                    },
                    containerColor = ComicTheme.colorScheme.surface,
                    contentWindowInsets = WindowInsets(),
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.union(
                                WindowInsets(0.dp, 16.dp, 16.dp, 16.dp)
                            )
                        )
                        .clip(
                            if (scaffoldState.navigationSuiteType.isNavigationBar) RoundedCornerShape(
                                0
                            ) else ComicTheme.shapes.extraLarge
                        )
                ) { contentPadding ->
                    LazyColumn(
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(
                            if (scaffoldState.navigationSuiteType.isNavigationBar) 0.dp else 8.dp
                        ),
                        modifier = Modifier
                    ) {
                        items(20) {
                            ListItem(
                                headlineContent = {
                                    Text("Item $it")
                                },
                                leadingContent = {
                                    Icon(ComicIcons.Info, null)
                                },
                                trailingContent = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            navigator.navigateTo(
                                                SupportingPaneScaffoldRole.Extra,
                                                "aaaaaa"
                                            )
                                        }
                                    }) {
                                        Icon(ComicIcons.MoreVert, null)
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                modifier = Modifier.clickable {
                                }
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                CenteredAppBar(
                    navigationSuiteType = LocalScaffoldState.current.navigationSuiteType,
                    title = {
                        Text("Detail Page")
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = LocalContainerColor.current,
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { contentPadding ->
            val layoutDirection = LocalLayoutDirection.current
            var extPaddings by remember(scaffoldState.navigationSuiteType.isNavigationRail, scaffoldState.navigationSuiteScaffoldState.targetValue, navigator.scaffoldState.targetState.tertiary) {
                mutableStateOf(
                    if (scaffoldState.navigationSuiteType.isNavigationRail) {
                        contentPadding.plus(
                            PaddingValues(
                                start = if (scaffoldState.navigationSuiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Hidden) 16.dp else 0.dp,
                                end = if (navigator.scaffoldState.targetState.tertiary == PaneAdaptedValue.Expanded) 0.dp else 16.dp,
                                bottom = 16.dp
                            ), layoutDirection
                        )
                    } else {
                        contentPadding
                    }
                )
            }

            val pad by animatePaddingValues(extPaddings)
            LazyColumn(
                contentPadding = pad,
                verticalArrangement = Arrangement.spacedBy(
                    if (scaffoldState.navigationSuiteType.isNavigationBar) 0.dp else 8.dp
                ),
                modifier = Modifier
            ) {
                items(20) {
                    if (scaffoldState.navigationSuiteType.isNavigationBar) {
                        ListItem(
                            headlineContent = {
                                Text("Item $it")
                            },
                            leadingContent = {
                                Icon(ComicIcons.Info, null)
                            },
                            trailingContent = {
                                IconButton(onClick = {
                                    scope.launch {
                                        navigator.navigateTo(
                                            SupportingPaneScaffoldRole.Extra,
                                            "aaaaaa"
                                        )
                                    }
                                }) {
                                    Icon(ComicIcons.MoreVert, null)
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable {
                            }
                        )
                    } else {
                        Card(
                            onClick = { }
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text("Item $it")
                                },
                                leadingContent = {
                                    Icon(ComicIcons.Info, null)
                                },
                                trailingContent = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            navigator.navigateTo(
                                                SupportingPaneScaffoldRole.Extra,
                                                "aaaaaa"
                                            )
                                        }
                                    }) {
                                        Icon(ComicIcons.MoreVert, null)
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            )
                        }
                    }
                }
            }
        }
    }
}
