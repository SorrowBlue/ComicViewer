package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.BottomNavigation
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ButtonsAlt
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.SideNavigation
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.animatePaddingValues
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationSuiteScaffoldSample() {
    ComicTheme {
        val state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState()
        var fabVisibility by remember { mutableStateOf(true) }
        var navigationSuiteType by remember { mutableStateOf(NavigationSuiteType.ShortNavigationBarMedium) }
        val containerColor by animateColorAsState(
            if (navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
        )
        NavigationSuiteScaffold(
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                wideNavigationRailColors = WideNavigationRailDefaults.colors(
                    containerColor = ComicTheme.colorScheme.surfaceContainer
                )
            ),
            containerColor = containerColor,
            state = state,
            navigationSuiteType = navigationSuiteType,
            navigationItems = {
                repeat(5) {
                    NavigationSuiteItem(
                        navigationSuiteType = navigationSuiteType,
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(ComicIcons.Info, null)
                        },
                        label = {
                            Text("Item $it")
                        }
                    )
                }
            },
            primaryActionContent = {
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier.animateFloatingActionButton(
                        visible = navigationSuiteType.isNavigationBar && fabVisibility,
                        alignment = Alignment.BottomEnd
                    )
                ) {
                    Icon(
                        imageVector = ComicIcons.Add,
                        contentDescription = null
                    )
                }
                AnimatedVisibility(navigationSuiteType.isNavigationRail) {
                    Column {
                        IconButton(
                            onClick = {
                                navigationSuiteType =
                                    if (navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed) {
                                        NavigationSuiteType.WideNavigationRailExpanded
                                    } else {
                                        NavigationSuiteType.WideNavigationRailCollapsed
                                    }
                            },
                            modifier = Modifier.padding(start = 24.dp)
                        ) {
                            Icon(
                                if (navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed) {
                                    ComicIcons.Menu
                                } else {
                                    ComicIcons.MenuOpen
                                },
                                null
                            )
                        }
                        ExtendedFloatingActionButton(
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .animateFloatingActionButton(
                                    visible = navigationSuiteType.isNavigationRail && fabVisibility,
                                    alignment = Alignment.Center
                                ),
                            expanded = navigationSuiteType != NavigationSuiteType.WideNavigationRailCollapsed,
                            onClick = {
                            },
                            text = { Text(text = "ADd") },
                            icon = {
                                Icon(
                                    imageVector = ComicIcons.Add,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            },
            modifier = Modifier
                .animateBounds(LocalAppState.current)
        ) {
//            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            val textFieldState = rememberTextFieldState()
            val searchBarState = rememberSearchBarState()
            val scope = rememberCoroutineScope()
            val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

            val inputField: @Composable () -> Unit =
                @Composable {
                    SearchBarDefaults.InputField(
                        modifier = Modifier,
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                        placeholder = { Text("Search...") },
                        leadingIcon = {
                            if (searchBarState.currentValue == SearchBarValue.Expanded) {
                                IconButton(
                                    onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                                ) {
                                    Icon(ComicIcons.ArrowBack, contentDescription = "Back")
                                }
                            } else {
                                Icon(ComicIcons.Search, contentDescription = null)
                            }
                        },
                        trailingIcon = { Icon(ComicIcons.MoreVert, contentDescription = null) },
                    )
                }

            Scaffold(
                topBar = {
                    CenteredSearchBar(
                        navigationSuiteType = navigationSuiteType,
                        scrollBehavior = scrollBehavior,
                        state = searchBarState,
                        inputField = inputField
                    ) {
                        LazyColumn(modifier = Modifier) {
                            items(20) {
                                ListItem(
                                    headlineContent = {
                                        Text("Item $it")
                                    },
                                    leadingContent = {
                                        Icon(ComicIcons.Info, null)
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    modifier = Modifier.clickable {}
                                )
                            }
                        }
                    }
                },
                containerColor = containerColor,
                contentWindowInsets = WindowInsets.safeDrawing,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { contentPadding ->
                val extPaddings = context(LocalLayoutDirection.current) {
                    if (navigationSuiteType.isNavigationRail) {
                        contentPadding + PaddingValues(
                            start = if (state.targetValue == NavigationSuiteScaffoldValue.Hidden) 16.dp else 0.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    } else {
                        contentPadding
                    }
                }
                val pad by animatePaddingValues(extPaddings)
                LazyColumn(
                    contentPadding = pad,
                    verticalArrangement = Arrangement.spacedBy(
                        if (navigationSuiteType.isNavigationBar) 0.dp else 8.dp
                    ),
                    modifier = Modifier
                ) {
                    items(20) {
                        if (navigationSuiteType.isNavigationBar) {
                            ListItem(
                                headlineContent = {
                                    Text("Item $it")
                                },
                                leadingContent = {
                                    Icon(ComicIcons.Info, null)
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                modifier = Modifier.clickable {}
                            )
                        } else {
                            Card(
                                onClick = {}
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text("Item $it")
                                    },
                                    leadingContent = {
                                        Icon(ComicIcons.Info, null)
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                )
                            }
                        }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ToggleButton(
                    checked = fabVisibility,
                    onCheckedChange = {
                        fabVisibility = !fabVisibility
                    }
                ) {
                    if (fabVisibility) {
                        Icon(
                            ComicIcons.ButtonsAlt,
                            null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text("FAB 表示中")
                    } else {
                        Icon(
                            ComicIcons.ButtonsAlt,
                            null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text("FAB 非表示中")
                    }
                }
                ToggleButton(
                    checked = state.targetValue == NavigationSuiteScaffoldValue.Visible,
                    onCheckedChange = {
                        scope.launch {
                            state.toggle()
                        }

                    }
                ) {
                    if (state.targetValue == NavigationSuiteScaffoldValue.Visible) {
                        Icon(
                            ComicIcons.Visibility,
                            null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text("ナビゲーション 表示中")
                    } else {
                        Icon(
                            ComicIcons.VisibilityOff,
                            null,
                            modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text("ナビゲーション 非表示中")
                    }
                }
                ButtonGroup(
                    overflowIndicator = {},
                    modifier = Modifier.fillMaxWidth(),
                    expandedRatio = 0.05f,
                    horizontalArrangement = Arrangement.Center
                ) {
                    toggleableItem(
                        checked = navigationSuiteType == NavigationSuiteType.ShortNavigationBarCompact,
                        label = "BarCompact",
                        icon = { Icon(ComicIcons.BottomNavigation, null) },
                        onCheckedChange = {
                            scope.launch {
                                state.hide()
                                navigationSuiteType = NavigationSuiteType.ShortNavigationBarCompact
                                state.show()
                            }
                        }
                    )
                    toggleableItem(
                        checked = navigationSuiteType == NavigationSuiteType.ShortNavigationBarMedium,
                        label = "BarMedium",
                        icon = { Icon(ComicIcons.BottomNavigation, null) },
                        onCheckedChange = {
                            scope.launch {
                                state.hide()
                                navigationSuiteType = NavigationSuiteType.ShortNavigationBarMedium
                                state.show()
                            }
                        }
                    )
                    toggleableItem(
                        checked = navigationSuiteType.isNavigationRail,
                        label = "Rail",
                        icon = { Icon(ComicIcons.SideNavigation, null) },
                        onCheckedChange = {
                            scope.launch {
                                state.hide()
                                navigationSuiteType =
                                    NavigationSuiteType.WideNavigationRailCollapsed
                                state.show()
                            }
                        }
                    )
                }
            }
        }
    }
}
