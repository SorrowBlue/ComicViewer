package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.responsive.AppBarAction2
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar2
import kotlinx.collections.immutable.toPersistentList

internal enum class FavoriteListAction(
    override val icon: ImageVector,
    override val label: Int,
    override val description: Int,
) :
    AppBarAction2 {

    Settings(
        ComicIcons.Settings,
        com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_label_settings,
        com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_desc_open_settings
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteListAppBar(
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onSettingsClick: () -> Unit = {},
) {
    ResponsiveTopAppBar2(
        title = { Text(text = stringResource(id = R.string.favorite_title_list)) },
        actions = remember { FavoriteListAction.entries.toPersistentList() },
        onClick = {
            when (it) {
                FavoriteListAction.Settings -> onSettingsClick()
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}
