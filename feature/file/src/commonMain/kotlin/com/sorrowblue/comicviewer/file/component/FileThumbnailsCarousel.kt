package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import kotlin.math.max

@Composable
fun FileThumbnailsCarousel(
    lazyPagingItems: LazyPagingItems<out FileThumbnail>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    carouselState: CarouselState = rememberCarouselState(itemCount = {
        max(lazyPagingItems.itemCount, 1)
    }),
) {
    if (lazyPagingItems.itemCount == 0) {
        Box(modifier = modifier.padding(contentPadding), contentAlignment = Alignment.Center) {
            Card(modifier = Modifier.size(ItemWidth)) {
                Spacer(Modifier.weight(1f))
                Icon(
                    ComicIcons.BrokenImage,
                    null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)

                )
                Spacer(Modifier.weight(1f))
            }
        }
    } else {
        val navigationState = LocalNavigationState.current
        HorizontalMultiBrowseCarousel(
            state = carouselState,
            preferredItemWidth = ItemWidth,
            itemSpacing = ItemSpacing,
            contentPadding = contentPadding,
            modifier = modifier
        ) { index ->
            lazyPagingItems[index]?.let {
                FileThumbnailAsyncImage(
                    fileThumbnail = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(186.dp)
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
