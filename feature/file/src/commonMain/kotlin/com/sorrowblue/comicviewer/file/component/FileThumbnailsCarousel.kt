package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.common.isTouchable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import kotlin.math.max
import kotlinx.coroutines.delay

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
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(Modifier.weight(1f))
            }
        }
    } else {
        Box(modifier = modifier) {
            val navigationSuiteType =
                NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
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
                                if (navigationSuiteType.isNavigationBar) {
                                    ComicTheme.colorScheme.surfaceVariant
                                } else {
                                    ComicTheme.colorScheme.surfaceContainerHigh
                                },
                            ),
                    )
                }
            }

            if (!isTouchable) {
                rememberCoroutineScope()
                FilledTonalIconButton(
                    onPress = {
                        carouselState.animateScrollToItem(carouselState.currentItem - 1)
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                        .padding(
                            contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Start),
                        ),
                ) {
                    Icon(ComicIcons.ArrowLeft, null)
                }
                FilledTonalIconButton(
                    onPress = {
                        carouselState.animateScrollToItem(carouselState.currentItem + 1)
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .padding(
                            contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.End),
                        ),
                ) {
                    Icon(ComicIcons.ArrowRight, null)
                }
            }
        }
    }
}

@Composable
private fun FilledTonalIconButton(
    onPress: suspend () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnPress by rememberUpdatedState(onPress)
    val isPressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(300)
            while (true) {
                currentOnPress()
//                delay(10)
            }
        }
    }
    FilledTonalIconButton(
        onClick = { },
        interactionSource = interactionSource,
        modifier = modifier,
    ) {
        content()
    }
}

private val ItemWidth = 186.dp
private val ItemSpacing = 8.dp
