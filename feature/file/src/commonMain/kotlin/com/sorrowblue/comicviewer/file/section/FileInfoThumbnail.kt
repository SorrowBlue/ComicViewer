package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems

@Composable
internal fun FileInfoThumbnail(
    file: File,
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    modifier: Modifier = Modifier,
) {
    val navigationState = LocalNavigationState.current
    Box(modifier = modifier) {
        if (lazyPagingItems != null) {
            FileThumbnailsCarousel(
                lazyPagingItems = lazyPagingItems,
                contentPadding = PaddingValues(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
            )
        } else {
            FileThumbnailAsyncImage(
                fileThumbnail = FileThumbnail.from(file),
                modifier = Modifier
                    .height(186.dp)
                    .clip(RoundedCornerShape(16.dp))
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
