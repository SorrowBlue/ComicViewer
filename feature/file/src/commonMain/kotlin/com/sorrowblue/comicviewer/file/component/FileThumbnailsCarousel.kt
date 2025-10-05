package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.common.isTouchable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import kotlin.math.max
import kotlinx.coroutines.launch

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
        Box(modifier = modifier) {
            val navigationState = LocalNavigationState.current
            HorizontalMultiBrowseCarousel(
                state = carouselState,
                preferredItemWidth = ItemWidth,
                itemSpacing = ItemSpacing,
                contentPadding = contentPadding,
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

            if (!isTouchable) {
                val scope = rememberCoroutineScope()
                val density = LocalDensity.current
                val size = remember { with(density) { 186.dp.toPx() } }
                IconButton(
                    onClick = { scope.launch { carouselState.animateScrollBy(size * -1) } },
                    modifier = Modifier.align(Alignment.CenterStart),
                    colors = IconButtonDefaults.filledTonalIconButtonColors().run {
                        copy(containerColor = containerColor.copy(alpha = 0.5f))
                    }
                ) {
                    Icon(ComicIcons.ArrowLeft, null)
                }
                IconButton(
                    onClick = { scope.launch { carouselState.animateScrollBy(size) } },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = IconButtonDefaults.filledTonalIconButtonColors().run {
                        copy(containerColor = containerColor.copy(alpha = 0.5f))
                    }
                ) {
                    Icon(ComicIcons.ArrowRight, null)
                }
            }
        }
    }
}

private val ItemWidth = 186.dp
private val ItemSpacing = 8.dp
