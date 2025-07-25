package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar

@Composable
fun TopAppBarWithNavigationBar(
    navigationSuiteType: NavigationSuiteType,
    scrollBehavior: TopAppBarScrollBehavior?) {
    TopAppBar(
        colors = if (navigationSuiteType.isNavigationBar) TopAppBarDefaults.topAppBarColors() else TopAppBarDefaults.topAppBarColors(
            containerColor = ComicTheme.colorScheme.surface
        ),
        title = {
            Text("Title")
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(ComicIcons.ArrowBack, null)
            }
        },
        actions = {
            AppBarRow(
                maxItemCount = 3,
                overflowIndicator = {
                    IconButton(onClick = { it.show() }) {
                        Icon(
                            imageVector = ComicIcons.MoreVert,
                            contentDescription = "Localized description",
                        )
                    }
                }
            ) {
                clickableItem(
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = ComicIcons.WatchLater,
                            contentDescription = null,
                        )
                    },
                    label = "Attachment",
                )

                clickableItem(
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = ComicIcons.Edit,
                            contentDescription = null
                        )
                    },
                    label = "Edit",
                )

                clickableItem(
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = ComicIcons.Favorite,
                            contentDescription = null
                        )
                    },
                    label = "Favorite",
                )

                clickableItem(
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = ComicIcons.SortByAlpha,
                            contentDescription = null
                        )
                    },
                    label = "Alarm",
                )

                clickableItem(
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = ComicIcons.RestartAlt,
                            contentDescription = "Localized description",
                        )
                    },
                    label = "Email",
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
