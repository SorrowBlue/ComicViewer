package com.sorrowblue.comicviewer.feature.favorite.add.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.add.component.RecentFavorite
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import comicviewer.feature.favorite.add.generated.resources.Res
import comicviewer.feature.favorite.add.generated.resources.favorite_add_label_recent
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RecentFavoriteSheet(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onClick: (Favorite) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider()
        Text(
            text = stringResource(Res.string.favorite_add_label_recent),
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier.padding(ComicTheme.dimension.padding)
        )
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { lazyPagingItems.itemCount },
            preferredItemWidth = 72.dp,
            itemSpacing = ComicTheme.dimension.padding,
            contentPadding = PaddingValues(ComicTheme.dimension.padding)
        ) {
            if (lazyPagingItems.itemCount <= 0) return@HorizontalMultiBrowseCarousel
            lazyPagingItems[it]?.let { item ->
                RecentFavorite(favorite = item, onClick = { onClick(item) })
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = ComicTheme.dimension.padding)
                .padding(horizontal = ComicTheme.dimension.margin)
        )
    }
}
