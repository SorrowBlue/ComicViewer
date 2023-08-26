package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@Composable
fun FavoriteItem(favorite: Favorite, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(favorite.name) },
        supportingContent = {
            Text(
                pluralStringResource(
                    id = R.plurals.favorite_common_label_file_count,
                    count = favorite.count
                )
            )
        },
        leadingContent = {
            AsyncImage(model = favorite, null, Modifier.size(56.dp))
        }
    )
}

@Preview
@Composable
private fun PrivateFavoriteItem() {
    AppMaterialTheme {
        FavoriteItem(Favorite("Preview name"), {})
    }
}
