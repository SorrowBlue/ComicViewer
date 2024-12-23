package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalTopAppBar

@Composable
internal fun FavoriteListAppBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = { Text(text = stringResource(id = R.string.favorite_title_list)) },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = ComicIcons.Settings,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState
    )
}
