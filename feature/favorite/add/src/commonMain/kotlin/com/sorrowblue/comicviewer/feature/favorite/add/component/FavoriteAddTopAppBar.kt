package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.favorite.add.generated.resources.Res
import comicviewer.feature.favorite.add.generated.resources.favorite_add_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FavoriteAddTopAppBar(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(Res.string.favorite_add_title)) },
        actions = {
            IconButton(onClick = onCloseClick) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            }
        },
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
