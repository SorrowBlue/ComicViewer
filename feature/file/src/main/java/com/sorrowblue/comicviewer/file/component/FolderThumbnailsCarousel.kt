package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail

@Composable
internal fun FolderThumbnailsCarousel(
    lazyPagingItems: LazyPagingItems<BookThumbnail>,
    modifier: Modifier = Modifier,
    carouselState: CarouselState = rememberCarouselState(itemCount = lazyPagingItems::itemCount),
) {
    HorizontalMultiBrowseCarousel(
        state = carouselState,
        preferredItemWidth = ItemWidth,
        itemSpacing = ItemSpacing,
        contentPadding = PaddingValues(horizontal = HorizontalPadding, vertical = VerticalPadding),
        modifier = modifier
    ) { index ->
        if (0 < lazyPagingItems.itemCount) {
            lazyPagingItems[index]?.let {
                FileThumbnailAsyncImage(
                    fileThumbnail = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .maskClip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

private val ItemWidth = 186.dp
private val ItemSpacing = 8.dp
private val HorizontalPadding = 16.dp
private val VerticalPadding = 8.dp
