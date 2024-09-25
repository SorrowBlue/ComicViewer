package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.previewPainter

@Composable
internal fun FolderThumbnailsCarousel(
    lazyPagingItems: LazyPagingItems<BookThumbnail>,
    modifier: Modifier = Modifier,
    carouselState: CarouselState = rememberCarouselState(itemCount = lazyPagingItems::itemCount),
) {
    HorizontalUncontainedCarousel(
        state = carouselState,
        itemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier
    ) { index ->
        if (lazyPagingItems.itemCount <= 0) return@HorizontalUncontainedCarousel
        lazyPagingItems[index]?.let {
            SubcomposeAsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = {
                    if (LocalInspectionMode.current) {
                        Image(painter = previewPainter(), contentDescription = null)
                    } else {
                        Icon(
                            imageVector = ComicIcons.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                    }
                },
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .maskClip(MaterialTheme.shapes.medium)
            )
        }
    }
}
