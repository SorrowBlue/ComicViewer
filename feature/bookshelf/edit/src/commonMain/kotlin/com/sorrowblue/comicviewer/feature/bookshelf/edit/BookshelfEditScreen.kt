/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SelectionList
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.wizard.BookshelfWizardPage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.wizard.BookshelfWizardScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.material3.AdaptiveAlertDialog2
import com.sorrowblue.comicviewer.framework.ui.material3.HorizontalPagerIndicator
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.rememberForm

@NavDestination(BookshelfWizardNavKey.Edit::class)
@Composable
internal fun BookshelfEditScreen(
    uiState: BookshelfWizardScreenUiState,
    pages: SnapshotStateList<BookshelfWizardPage>,
    pagerState: PagerState,
    actionButton: @Composable () -> Unit = {},
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    onBack: () -> Unit,
    content: @Composable (BookshelfWizardPage, PaddingValues) -> Unit,
) {
    AdaptiveAlertDialog2(
        title = {
            Text(uiState.title)
        },
        onBackClick = {
            onBack()
        },
        actionButton = actionButton,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    ) { contentPadding ->
        Box {
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                contentPadding = PaddingValues(),
            ) { pageIndex ->
                content(pages[pageIndex], contentPadding.plus(PaddingValues(top = 16.dp)))
            }
            AnimatedVisibility(pages.size > 1, enter = fadeIn(), exit = fadeOut()) {
                Row {
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        pageCount = pagerState.pageCount,
                        indicatorWidth = 24.dp,
                        indicatorHeight = 8.dp,
                        spacing = 8.dp,
                        indicatorShape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(
                                contentPadding.only(
                                    PaddingValuesSides.Top + PaddingValuesSides.Horizontal,
                                ),
                            ),
                    )
                }
            }
        }
    }
}

@Suppress("unused")
@NavDestination(BookshelfWizardNavKey.Selection::class)
@Composable
private fun BookshelfWizardSelectionScreen() {
    // For NavGraph Preview
}

@NavPreview(BookshelfWizardNavKey.Selection::class, primary = true)
@Preview(device = Devices.PIXEL_9, name = "Selection")
@Preview(device = Devices.PIXEL_TABLET, name = "Selection")
@Composable
private fun BookshelfEditScreenSelectionPreview() {
    PreviewTheme {
        Box(Modifier.fillMaxSize())
        BookshelfEditScreen(
            uiState = BookshelfWizardScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                    BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            pagerState = rememberPagerState(0) { 2 },
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

@NavPreview(BookshelfWizardNavKey.Edit::class, primary = true)
@Preview(device = Devices.PIXEL_9, name = "SmbEditor")
@Preview(device = Devices.PIXEL_TABLET, name = "SmbEditor")
@Composable
private fun BookshelfEditScreenSmbEditorPreview() {
    PreviewTheme {
        Box(Modifier.fillMaxSize())
        BookshelfEditScreen(
            uiState = BookshelfWizardScreenUiState(
                title = "Register",
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                    BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            pagerState = rememberPagerState(0) { 2 },
            onBack = {},
            content = { _, contentPadding ->
                SmbEditorContents(
                    form = rememberForm(
                        initialValue = SmbEditForm(auth = SmbEditForm.Auth.UserPass),
                    ) {},
                    uiState = BookshelfEditScreenUiState(progress = false),
                    modifier = Modifier.fillMaxHeight()
                        .padding(
                            contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                                .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                        ),
                )
            }
        )
    }
}

@NavPreview(BookshelfWizardNavKey.Edit::class)
@Preview(device = Devices.PIXEL_9, name = "DeviceEditor")
@Preview(device = Devices.PIXEL_TABLET, name = "DeviceEditor")
@Composable
private fun BookshelfEditScreenDeviceEditorPreview() {
    PreviewTheme {
        Box(Modifier.fillMaxSize())
        BookshelfEditScreen(
            uiState = BookshelfWizardScreenUiState(
                title = "Register",
            ),
            pages = remember {
                mutableStateListOf(
                    BookshelfWizardPage.Selection,
                    BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            pagerState = rememberPagerState(1) { 2 },
            onBack = {},
            content = { _, contentPadding ->
                val form = rememberForm(initialValue = DeviceEditForm()) {}
                DeviceEditorContents(
                    form = form,
                    folderSelectFieldState = rememberFolderSelectFieldState(
                        form = form,
                        onOpenDocumentTreeCancel = {},
                    ),
                    uiState = BookshelfEditScreenUiState(progress = false),
                    modifier = Modifier.fillMaxHeight()
                        .padding(
                            contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                                .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                        ),
                )
            }
        )
    }
}
