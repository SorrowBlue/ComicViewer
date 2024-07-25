package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.DocumentUnknown
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun FavoriteItem(favorite: Favorite, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        ),
        headlineContent = { Text(favorite.name) },
        supportingContent = {
            Text(
                pluralStringResource(
                    id = R.plurals.favorite_common_label_file_count,
                    count = favorite.count,
                    favorite.count
                )
            )
        },
        leadingContent = {
            SubcomposeAsyncImage(
                model = favorite,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp),
                error = {
                    Icon(imageVector = ComicIcons.Image, contentDescription = null)
                },
                loading = {
                    Icon(imageVector = ComicIcons.DocumentUnknown, contentDescription = null)
                }
            )
        },
        trailingContent = {
            if (favorite.exist) {
                Icon(imageVector = ComicIcons.Check, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
private fun PrivateFavoriteItem() {
    ComicTheme {
        FavoriteItem(Favorite("Preview name"), {})
    }
}
