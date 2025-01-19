package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold

@PreviewMultiScreen
@Composable
private fun PreviewFileInfoSheet(
    @PreviewParameter(FileInfoUiStateProvider::class) uiState: FileInfoUiState,
) {
    PreviewCanonicalScaffold(
        navigator = rememberSupportingPaneScaffoldNavigator(
            initialDestinationHistory = listOf(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    uiState
                )
            )
        ),
        extraPane = { contentKey ->
            val screenState = rememberScrollState()
            val lazyPagingItems = PagingData.flowData(10) { BookThumbnail.from(fakeBookFile(it)) }
            FileInfoSheet(
                uiState = contentKey,
                onAction = {},
                scrollState = screenState,
                lazyPagingItems = lazyPagingItems.collectAsLazyPagingItems()
            )
        }
    ) {
    }
}

private class FileInfoUiStateProvider : PreviewParameterProvider<FileInfoUiState> {
    val attribute = FileAttribute(
        archive = true,
        compressed = true,
        hidden = true,
        normal = true,
        directory = true,
        readonly = true,
        sharedRead = true,
        system = true,
        temporary = true,
        volume = true,
    )
    override val values: Sequence<FileInfoUiState>
        get() = sequenceOf(
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = false, loading = true),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = false, loading = false),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = true, loading = true),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = true, loading = false),
                isOpenFolderEnabled = true
            ),
        )
}
