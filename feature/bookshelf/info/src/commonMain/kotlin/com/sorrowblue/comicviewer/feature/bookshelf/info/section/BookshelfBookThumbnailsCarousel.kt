package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults

@Composable
internal fun BookshelfBookThumbnailsCarousel(
    pagingItems: LazyPagingItems<BookThumbnail>,
    modifier: Modifier = Modifier,
    carouselState: CarouselState = rememberCarouselState(itemCount = pagingItems::itemCount),
) {
    HorizontalUncontainedCarousel(
        state = carouselState,
        itemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
        modifier = modifier
    ) { index ->
        if (pagingItems.itemCount <= 0) return@HorizontalUncontainedCarousel
        pagingItems[index]?.let {
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .maskClip(MaterialTheme.shapes.medium)
            ) {
                FileThumbnailAsyncImage(
                    fileThumbnail = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
