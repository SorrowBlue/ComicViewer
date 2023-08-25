package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.compose.material3.rememberOverflowMenuState
import com.sorrowblue.comicviewer.framework.resource.R


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
                    text = { Text(stringResource(R.string.framework_title_settings)) },
                    trailingIcon = {
                        Icon(
                            Icons.TwoTone.Settings,
                            stringResource(R.string.framework_title_settings)
                        )
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
