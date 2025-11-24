package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContentsUiState
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfErrorContents(modifier: Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator()
    }
}

internal sealed interface BookshelfInfoSheetUiState {
    data object Loading : BookshelfInfoSheetUiState

    data object Error : BookshelfInfoSheetUiState

    data class Loaded(val bookshelfFolder: BookshelfFolder) : BookshelfInfoSheetUiState
}

@Composable
internal fun BookshelfInfoScreen(
    uiState: BookshelfInfoSheetUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
        onCloseClick = onBackClick,
        actions = {
            BottomActions(
                onEditClick = onEditClick,
                onRemoveClick = onRemoveClick,
                enabled = uiState is BookshelfInfoSheetUiState.Loaded,
            )
        },
        content = content,
    )
}

@Preview
@Composable
private fun BookshelfInfoScreenPreview() {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
        onCloseClick = { },
        actions = {
            BottomActions(
                onEditClick = {},
                onRemoveClick = {},
                enabled = true,
            )
        },
    ) { contentPadding ->
        BookshelfInfoContents(
            uiState = BookshelfInfoContentsUiState(fakeSmbServer(), fakeFolder()),
            lazyPagingItems = PagingData.flowData { FileThumbnail.from(fakeBookFile()) }
                .collectAsLazyPagingItems(),
            onScanFileClick = {},
            onScanThumbnailClick = { },
            contentPadding = contentPadding,
        )
    }
}
