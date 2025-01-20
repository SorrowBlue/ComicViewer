package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import comicviewer.feature.favorite.generated.resources.Res
import comicviewer.feature.favorite.generated.resources.favorite_title_list
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FavoriteListAppBar(
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = { Text(text = stringResource(Res.string.favorite_title_list)) },
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
