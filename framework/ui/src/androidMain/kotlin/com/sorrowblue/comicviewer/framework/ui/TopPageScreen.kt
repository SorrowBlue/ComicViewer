package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton

@Composable
fun TopPageScreen(onClickItem: () -> Unit) {
    val scaffoldState = LocalScaffoldState.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            CenteredAppBar(
                navigationSuiteType = LocalScaffoldState.current.navigationSuiteType,
                title = { Text("Top Page") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackIconButton(onClick = {})
                },
                actions = {
                    repeat(5) {
                        clickableItem(
                            onClick = {},
                            label = { Text("Settings") },
                            icon = { Icon(ComicIcons.Settings, null) }
                        )
                    }
                }
            )
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
