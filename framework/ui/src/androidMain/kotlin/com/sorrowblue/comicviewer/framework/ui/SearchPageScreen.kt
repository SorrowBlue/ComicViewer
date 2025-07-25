package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.LocalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.layout.animatePaddingValues
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import kotlinx.coroutines.launch

@Composable
fun SearchPageScreen(onClickItem: () -> Unit) {
    val scaffoldState = LocalScaffoldState.current
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
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
                navigationSuiteType = LocalScaffoldState.current.navigationSuiteType,
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
        containerColor = LocalContainerColor.current,
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val extPaddings = context(LocalLayoutDirection.current) {
            if (scaffoldState.navigationSuiteType.isNavigationRail) {
                contentPadding + PaddingValues(
                    start = if (scaffoldState.navigationSuiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Hidden) 16.dp else 0.dp,
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
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable {
                            onClickItem()
                        }
                    )
                } else {
                    Card(
                        onClick = onClickItem
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
}
