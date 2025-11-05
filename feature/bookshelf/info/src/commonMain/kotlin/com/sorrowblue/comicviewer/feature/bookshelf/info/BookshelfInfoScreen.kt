package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContentsUiState
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_title
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun BookshelfErrorContents(modifier: Modifier) {
    TODO("Not yet implemented")
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
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
        onCloseClick = onBackClick,
        actions = {
            BottomActions(
                onEditClick = onEditClick,
                onRemoveClick = onRemoveClick,
                enabled = uiState is BookshelfInfoSheetUiState.Loaded
            )
        },
        content = content
    )
}

@Preview
@Composable
private fun BookshelfInfoSheetPreview() {
    val navigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "")
        )
    )
    val uiState =
        BookshelfInfoSheetUiState.Loaded(BookshelfFolder(fakeSmbServer(), fakeFolder()))
    PreviewCanonicalScaffold(
        navigator = navigator,
        extraPane = {
            BookshelfInfoScreen(
                uiState = uiState,
                onBackClick = {},
                onEditClick = {},
                onRemoveClick = {},
            ) { contentPadding ->
                BookshelfInfoContents(
                    uiState = BookshelfInfoContentsUiState(
                        uiState.bookshelfFolder.bookshelf,
                        uiState.bookshelfFolder.folder
                    ),
                    lazyPagingItems = PagingData.flowData(10) {
                        BookThumbnail.from(fakeBookFile())
                    }.collectAsLazyPagingItems(),
                    onScanFileClick = {},
                    onScanThumbnailClick = {},
                    contentPadding = contentPadding,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    ) {
    }
}
