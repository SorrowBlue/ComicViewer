package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.file.component.FileAttributeChips
import com.sorrowblue.comicviewer.file.section.FileInfoList
import com.sorrowblue.comicviewer.file.section.FileInfoThumbnail
import com.sorrowblue.comicviewer.file.section.SheetActionButtons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults

@Composable
internal fun ErrorContents() {

}

@Composable
internal fun LoadingContents() {

}

@Composable
internal fun FileInfoScreen(
    uiState: FileInfoScreenUiState,
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    onBackClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onOpenFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val file = uiState.file
    ExtraPaneScaffold(
        title = { Text(text = file.name) },
        onCloseClick = onBackClick,
        modifier = modifier
    ) { contentPadding ->
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                FileInfoThumbnail(
                    file = file,
                    lazyPagingItems = lazyPagingItems,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                SheetActionButtons(
                    uiState = uiState.sheetActionButtonsUiState,
                    onReadLaterClick = onReadLaterClick,
                    onCollectionClick = onCollectionClick,
                    onOpenFolderClick = onOpenFolderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            layoutDirection = LocalLayoutDirection.current,
                            horizontal = contentPadding
                        )
                        .padding(top = ComicTheme.dimension.padding * 2)
                        .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
                )
                FileInfoList(
                    file = file,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            layoutDirection = LocalLayoutDirection.current,
                            horizontal = contentPadding
                        )
                        .padding(top = ComicTheme.dimension.padding * 2)
                        .padding(horizontal = 8.dp),
                )
                uiState.attribute?.let {
                    FileAttributeChips(
                        it,
                        modifier = Modifier
                            .padding(
                                layoutDirection = LocalLayoutDirection.current,
                                horizontal = contentPadding
                            )
                            .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
                    )
                }
                Spacer(
                    modifier = Modifier.height(
                        contentPadding.calculateBottomPadding() + ExtraPaneScaffoldDefaults.HorizontalPadding
                    )
                )
            }
        }
    }
}

private fun Modifier.padding(
    layoutDirection: LayoutDirection,
    horizontal: PaddingValues,
): Modifier {
    val start: Dp
    val end: Dp
    with(layoutDirection) {
        start = horizontal.calculateStartPadding(this)
        end = horizontal.calculateEndPadding(this)
    }
    return this.padding(start = start, end = end)
}
