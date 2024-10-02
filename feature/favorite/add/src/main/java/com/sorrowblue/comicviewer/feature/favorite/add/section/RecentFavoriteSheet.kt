package com.sorrowblue.comicviewer.feature.favorite.add.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.add.R
import com.sorrowblue.comicviewer.feature.favorite.add.component.RecentFavorite
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlin.math.min

@Composable
internal fun RecentFavoriteSheet(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onClick: (Favorite) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider()
        Text(
            text = stringResource(R.string.favorite_add_label_recent),
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier.padding(ComicTheme.dimension.padding)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(
                ComicTheme.dimension.padding,
                Alignment.CenterHorizontally
            ),
            contentPadding = PaddingValues(horizontal = ComicTheme.dimension.margin),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                count = min(lazyPagingItems.itemCount, 5),
                key = lazyPagingItems.itemKey { it.id.value }
            ) {
                lazyPagingItems[it]?.let { item ->
                    RecentFavorite(favorite = item, onClick = { onClick(item) })
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = ComicTheme.dimension.padding)
                .padding(horizontal = ComicTheme.dimension.margin)
        )
    }
}
