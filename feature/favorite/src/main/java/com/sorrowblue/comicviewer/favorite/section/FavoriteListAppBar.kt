package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteListAppBar(
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onSettingsClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text("Favorite") },
        actions = {
            val overflowMenuState = rememberOverflowMenuState()
            OverflowMenu(overflowMenuState) {
                DropdownMenuItem(
                    text = { Text("Open Settings") },
                    trailingIcon = {
                        Icon(ComicIcons.Settings, "Open Settings")
                    },
                    onClick = {
                        overflowMenuState.collapse()
                        onSettingsClick()
                    }
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}
