package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite

@Composable
internal fun RecentFavorite(
    favorite: Favorite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Box {
            SubcomposeAsyncImage(
                model = favorite,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp),
                error = {
                    Icon(
                        imageVector = ComicIcons.Image,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            )
            if (favorite.exist) {
                Icon(
                    imageVector = ComicIcons.CheckCircle,
                    contentDescription = null,
                    tint = ComicTheme.colorScheme.primaryContainer,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
        Text(
            text = favorite.name,
            style = ComicTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text =
            pluralStringResource(
                id = com.sorrowblue.comicviewer.feature.favorite.common.R.plurals.favorite_common_label_file_count,
                count = favorite.count,
                favorite.count
            ),
            style = ComicTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun PreviewRecentFavorite(@PreviewParameter(BooleanProvider::class) exist: Boolean) {
    PreviewTheme {
        RecentFavorite(favorite = fakeFavorite(exist = exist), onClick = {})
    }
}

private class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
