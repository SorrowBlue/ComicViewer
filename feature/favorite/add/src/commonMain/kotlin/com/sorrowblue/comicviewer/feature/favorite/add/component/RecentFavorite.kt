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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.favorite.common.generated.resources.favorite_common_label_file_count
import org.jetbrains.compose.resources.pluralStringResource
import comicviewer.feature.favorite.common.generated.resources.Res as CommonRes

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
                CommonRes.plurals.favorite_common_label_file_count,
                favorite.count
            ),
            style = ComicTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
