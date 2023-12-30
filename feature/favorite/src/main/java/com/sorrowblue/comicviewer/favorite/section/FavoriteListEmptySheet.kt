package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.ui.EmptyContent

@Composable
internal fun FavoriteListEmptySheet(modifier: Modifier = Modifier) {
    EmptyContent(
        imageVector = ComicIcons.UndrawNoData,
        text = stringResource(id = R.string.favorite_list_label_no_favorites),
        modifier = modifier
    )
}
