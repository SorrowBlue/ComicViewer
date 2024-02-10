package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.add.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun FavoriteAddFab(onAddClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text(text = stringResource(R.string.favorite_add_btn_add)) },
        icon = { Icon(ComicIcons.Add, null) },
        onClick = onAddClick
    )
}
