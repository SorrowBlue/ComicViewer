package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.add.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun FavoriteAddTopAppBar(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.favorite_add_title),
            )
        },
        actions = {
            IconButton(onClick = onCloseClick) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            }
        },
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
