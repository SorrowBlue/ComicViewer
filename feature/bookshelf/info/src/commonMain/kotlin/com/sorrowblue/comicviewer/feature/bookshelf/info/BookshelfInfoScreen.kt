package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContents
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BookshelfInfoContentsUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeDeviceStorage
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfErrorContents(modifier: Modifier = Modifier) {
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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
        onCloseClick = onBackClick,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Preview(device = Devices.TABLET, locale = "en")
@Composable
private fun BookshelfInfoScreenPreview(
    @PreviewParameter(
        BookshelfProvider::class,
    ) bookshelfFolder: BookshelfFolder,
) {
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, ""),
        ),
    )
    PreviewTheme(true) {
        SupportingPaneScaffold(
            modifier = Modifier.background(ComicTheme.colorScheme.background),
            directive = scaffoldNavigator.scaffoldDirective,
            value = scaffoldNavigator.scaffoldValue,
            mainPane = {
                Spacer(modifier = Modifier.fillMaxSize())
            },
            supportingPane = {},
            extraPane = {
                ExtraPaneScaffold(
                    title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
                    onCloseClick = { },
                ) { contentPadding ->
                    BookshelfInfoContents(
                        uiState = BookshelfInfoContentsUiState(
                            bookshelfFolder.bookshelf,
                            bookshelfFolder.folder,
                        ),
                        lazyPagingItems = PagingData.flowData { FileThumbnail.from(fakeBookFile()) }
                            .collectAsLazyPagingItems(),
                        onScanFileClick = {},
                        onScanThumbnailClick = {},
                        onEditClick = {},
                        onDeleteClick = {},
                        contentPadding = contentPadding,
                    )
                }
            },
        )
    }
}

private class BookshelfProvider : PreviewParameterProvider<BookshelfFolder> {
    override val values: Sequence<BookshelfFolder>
        get() = sequenceOf(
            BookshelfFolder(fakeSmbServer(), fakeFolder()),
            BookshelfFolder(fakeDeviceStorage(), fakeFolder()),
        )
}
