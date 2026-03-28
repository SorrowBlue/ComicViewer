package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.file.section.FileAttributeCard
import com.sorrowblue.comicviewer.file.section.FileInfoButtons
import com.sorrowblue.comicviewer.file.section.FileInfoButtonsUiState
import com.sorrowblue.comicviewer.file.section.FileInfoList
import com.sorrowblue.comicviewer.file.section.FileInfoThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ErrorContents() {
    Text("Error")
}

@Composable
internal fun LoadingContents() {
    CircularProgressIndicator()
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
        title = { Text(text = "Book Details") },
        onCloseClick = onBackClick,
        modifier = modifier,
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = contentPadding.calculateTopPadding()),
        ) {
            FileInfoThumbnail(
                file = file,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding.only(PaddingValuesSides.Horizontal) +
                    PaddingValues(horizontal = ExtraPaneScaffoldDefaults.Padding),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.size(16.dp))
            FileInfoButtons(
                uiState = uiState.fileInfoButtonsUiState,
                onReadLaterClick = onReadLaterClick,
                onCollectionClick = onCollectionClick,
                onOpenFolderClick = onOpenFolderClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                    .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
            )
            Spacer(Modifier.size(16.dp))
            FileInfoList(
                file = file,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                    .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
            )
            uiState.attribute?.let { attribute ->
                Spacer(Modifier.size(16.dp))
                FileAttributeCard(
                    file = file,
                    fileAttribute = attribute,
                    modifier = Modifier
                        .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                        .padding(horizontal = ExtraPaneScaffoldDefaults.Padding),
                )
            }
            Spacer(
                modifier = Modifier.size(
                    contentPadding.calculateBottomPadding() + ExtraPaneScaffoldDefaults.Padding,
                ),
            )
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

@Preview(heightDp = 1600)
@Composable
private fun AdaptiveNavigationSuiteScaffoldPreview(
    @PreviewParameter(FileParameter::class) data: Pair<File, Flow<PagingData<BookThumbnail>>?>,
) {
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        initialDestinationHistory = listOf(
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
            ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, ""),
        ),
    )
    PreviewTheme {
        SupportingPaneScaffold(
            modifier = Modifier.background(ComicTheme.colorScheme.background),
            directive = scaffoldNavigator.scaffoldDirective,
            value = scaffoldNavigator.scaffoldValue,
            mainPane = {
                Spacer(modifier = Modifier.fillMaxSize())
            },
            supportingPane = {},
            extraPane = {
                FileInfoScreen(
                    uiState = FileInfoScreenUiState(
                        file = data.first,
                        attribute = FileAttribute(
                            true,
                            true,
                            true,
                            true,
                            true,
                            true,
                            true,
                            true,
                            true,
                            true,
                        ),
                        fileInfoButtonsUiState = FileInfoButtonsUiState(isOpenFolderEnabled = true),
                    ),
                    lazyPagingItems = data.second?.collectAsLazyPagingItems(),
                    onBackClick = {},
                    onReadLaterClick = {},
                    onCollectionClick = {},
                    onOpenFolderClick = {},
                )
            },
        )
    }
}

private class FileParameter :
    PreviewParameterProvider<Pair<File, Flow<PagingData<BookThumbnail>>?>> {
    override val values: Sequence<Pair<File, Flow<PagingData<BookThumbnail>>?>>
        get() = sequenceOf(
            fakeBookFile() to null,
            fakeFolder() to PagingData.flowData(10) {
                BookThumbnail.from(fakeBookFile())
            },
        )
}
