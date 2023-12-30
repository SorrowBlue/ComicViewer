package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.add.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun FavoriteAddTopAppBar(
    onBackClick: () -> Unit,
    appBarScrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.favorite_add_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ComicIcons.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = appBarScrollBehavior
    )
}
