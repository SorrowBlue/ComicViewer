package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeFavorite

@Composable
fun FavoriteListItem(
    favorite: Favorite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    FavoriteListItem(
        favorite = favorite,
        content = content,
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Composable
fun FavoriteListCardItem(
    favorite: Favorite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    Card(onClick = onClick, modifier = modifier) {
        FavoriteListItem(
            favorite = favorite,
            content = content,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun FavoriteListItem(
    favorite: Favorite,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    content: @Composable (() -> Unit)? = null,
) {
    ListItem(
        colors = colors,
        leadingContent = {
            Box {
                SubcomposeAsyncImage(
                    model = favorite,
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    error = {
                        Icon(
                            imageVector = ComicIcons.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier.wrapContentSize()
                        )
                    },
                    loading = if (LocalInspectionMode.current) {
                        null
                    } else {
                        {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(ComicTheme.shapes.medium)
                        .background(ComicTheme.colorScheme.surfaceVariant),
                )
                if (favorite.exist) {
                    Icon(
                        ComicIcons.CheckCircle,
                        null,
                        modifier = Modifier
                            .alpha(0.75f)
                            .clip(CircleShape)
                            .background(ComicTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        },
        headlineContent = {
            Text(text = favorite.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(
                pluralStringResource(
                    id = R.plurals.favorite_common_label_file_count,
                    count = favorite.count,
                    favorite.count
                )
            )
        },
        trailingContent = content,
        modifier = modifier
    )
}

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
