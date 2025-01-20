package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun RecentFavoritePreview(@PreviewParameter(BooleanProvider::class) exist: Boolean) {
    PreviewTheme {
        RecentFavorite(favorite = fakeFavorite(exist = exist), onClick = {})
    }
}

private class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
