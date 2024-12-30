package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState

@Composable
fun FileThumbnailsCarousel(
    lazyPagingItems: LazyPagingItems<out FileThumbnail>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    carouselState: CarouselState = rememberCarouselState(itemCount = lazyPagingItems::itemCount),
) {
    val navigationState = LocalNavigationState.current
    HorizontalMultiBrowseCarousel(
        state = carouselState,
        preferredItemWidth = ItemWidth,
        itemSpacing = ItemSpacing,
        contentPadding = contentPadding,
        modifier = modifier
    ) { index ->
        if (0 < lazyPagingItems.itemCount) {
            lazyPagingItems[index]?.let {
                FileThumbnailAsyncImage(
                    fileThumbnail = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .maskClip(MaterialTheme.shapes.medium)
                        .background(
                            if (navigationState is NavigationState.NavigationBar) {
                                ComicTheme.colorScheme.surfaceVariant
                            } else {
                                ComicTheme.colorScheme.surfaceContainerHigh
                            }
                        )
                )
            }
        }
    }
}

private val ItemWidth = 186.dp
private val ItemSpacing = 8.dp
