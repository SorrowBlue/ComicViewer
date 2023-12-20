package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun FavoriteAddFab(onAddClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text(text = "Add") },
        icon = { Icon(ComicIcons.Add, null) },
        onClick = onAddClick
    )
}
