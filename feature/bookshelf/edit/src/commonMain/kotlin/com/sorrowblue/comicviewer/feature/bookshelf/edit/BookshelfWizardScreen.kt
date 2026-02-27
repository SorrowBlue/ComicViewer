package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SelectionList
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.material3.AdaptiveAlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.HorizontalPagerIndicator
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.rememberForm

data class BookshelfWizardScreenUiState(
    val title: String = "",
    val showDiscardDialog: Boolean = false,
)

@Composable
internal fun BookshelfWizardScreen(
    uiState: BookshelfWizardScreenUiState,
    pages: SnapshotStateList<BookshelfWizardPage>,
    pagerState: PagerState,
    onBack: () -> Unit,
    content: @Composable (BookshelfWizardPage, PaddingValues) -> Unit,
) {
    AdaptiveAlertDialog(
        title = {
            Text(uiState.title)
        },
        onBackClick = {
            onBack()
        },
    ) { contentPadding ->
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(),
        ) { pageIndex ->
            content(pages[pageIndex], contentPadding.plus(PaddingValues(top = 8.dp)))
        }
        AnimatedVisibility(pages.size != 1, enter = fadeIn(), exit = fadeOut()) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                pageCount = pagerState.pageCount,
                indicatorWidth = 24.dp,
                indicatorHeight = 8.dp,
                spacing = 8.dp,
                indicatorShape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(
                        contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal),
                    ),
            )
        }
    }
}

@Preview(device = Devices.PIXEL_9, name = "Selection")
@Preview(device = Devices.PIXEL_TABLET, name = "Selection")
@Composable
private fun BookshelfWizardScreenSelectionPreview() {
    PreviewTheme {
        BookshelfWizardScreen(
            uiState = BookshelfWizardScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                )
            },
            pagerState = remember { PagerState { 1 } },
            onBack = {},
            content = { _, contentPadding ->
                SelectionList(
                    items = remember { List(4) { BookshelfType.entries }.flatten() },
                    onSourceClick = {},
                    contentPadding = contentPadding,
                )
            },
        )
    }
}

@Preview(device = Devices.PIXEL_9, name = "SmbEditor")
@Preview(device = Devices.PIXEL_TABLET, name = "SmbEditor")
@Composable
private fun BookshelfWizardScreenSmbEditorPreview() {
    PreviewTheme {
        BookshelfWizardScreen(
            uiState = BookshelfWizardScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                    BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            pagerState = remember { PagerState(1) { 2 } },
            onBack = {},
            content = { _, contentPadding ->
                BookshelfEditorContents(
                    onSaveClick = {},
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SmbEditorContents(
                        form = rememberForm(
                            initialValue = SmbEditForm(auth = SmbEditForm.Auth.UserPass),
                        ) {},
                        uiState = BookshelfEditScreenUiState(progress = false),
                    )
                }
            },
        )
    }
}

@Preview(device = Devices.PIXEL_9, name = "DeviceEditor")
@Preview(device = Devices.PIXEL_TABLET, name = "DeviceEditor")
@Composable
private fun BookshelfWizardScreenDeviceEditorPreview() {
    PreviewTheme {
        BookshelfWizardScreen(
            uiState = BookshelfWizardScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                    BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            pagerState = remember { PagerState(1) { 2 } },
            onBack = {},
            content = { _, contentPadding ->
                BookshelfEditorContents(
                    onSaveClick = {},
                    contentPadding = contentPadding,
                ) {
                    val form = rememberForm(initialValue = DeviceEditForm()) {}
                    DeviceEditorContents(
                        form = form,
                        folderSelectFieldState = rememberFolderSelectFieldState(
                            form = form,
                            onOpenDocumentTreeCancel = {},
                        ),
                        uiState = BookshelfEditScreenUiState(progress = false),
                    )
                }
            },
        )
    }
}
