package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite

@PreviewLightDark
@Composable
private fun FavoriteItemPreview() {
    PreviewTheme {
        FavoriteListItem(fakeFavorite(), onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun FavoriteItemCardPreview() {
    PreviewTheme {
        FavoriteListCardItem(fakeFavorite(), onClick = {})
    }
}
