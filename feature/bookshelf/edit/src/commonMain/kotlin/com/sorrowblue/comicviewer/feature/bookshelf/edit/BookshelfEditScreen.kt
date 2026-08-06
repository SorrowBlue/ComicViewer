/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.BookshelfEditorScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.DeviceEditorForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.SmbEditorForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.bookshelfEditorEntry
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.discordDialogEntry
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.drawDivider
import com.sorrowblue.comicviewer.feature.bookshelf.edit.type.bookshelfTypeEntry
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.material3.AdaptiveAlertDialog2
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_back
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register
import comicviewer.feature.bookshelf.edit.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.rememberForm

@NavDestination(BookshelfWizardNavKey.Edit::class)
@Composable
internal fun BookshelfEditScreen(
    uiState: BookshelfEditScreenUiState,
    backStack: List<NavKey>,
    onBack: () -> Unit,
    onComplete: () -> Unit = {},
    onTypeClick: (BookshelfType) -> Unit = {},
    updateCanSubmit: (Boolean) -> Unit = {},
    discardConfirm: (Boolean) -> Unit = {},
    onConfirm: (Boolean) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val eventFlow = remember { EventFlow<BookshelfEditScreenEvent>() }
    BookshelfEditScreen(
        uiState = uiState,
        backStack = backStack,
        onDismissRequest = {
            eventFlow.tryEmit(BookshelfEditScreenEvent.DismissRequest)
        },
        onBackClick = {
            eventFlow.tryEmit(BookshelfEditScreenEvent.BackClick)
        },
        onSaveClick = {
            eventFlow.tryEmit(BookshelfEditScreenEvent.SubmitClick)
        },
    ) { contentPadding ->
        val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
        val sceneStrategies = remember { listOf(dialogSceneStrategy) }
        NavDisplay(
            backStack = backStack,
            sceneStrategies = sceneStrategies,
            modifier = Modifier.fillMaxSize(),
            entryProvider = entryProvider {
                bookshelfTypeEntry(
                    eventFlow = eventFlow,
                    onBack = onBack,
                    onTypeClick = onTypeClick,
                    contentPadding = contentPadding,
                )
                bookshelfEditorEntry(
                    eventFlow = eventFlow,
                    onBack = onBack,
                    onComplete = onComplete,
                    updateCanSubmit = updateCanSubmit,
                    discardConfirm = discardConfirm,
                    contentPadding = contentPadding,
                )
                discordDialogEntry(
                    onDismissRequest = onDismissRequest,
                    onConfirm = onConfirm,
                )
            },
        )
    }
}

@NavDestination(BookshelfWizardNavKey.Edit::class)
@Composable
private fun BookshelfEditScreen(
    uiState: BookshelfEditScreenUiState,
    backStack: List<NavKey>,
    onDismissRequest: () -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    AdaptiveAlertDialog2(
        title = {
            Text(uiState.title)
        },
        onDismissRequest = onDismissRequest,
        navigationIcon = { BackIconButton(onClick = onBackClick) },
        dismissButton = {
            if (backStack.size == 1) {
                TextButton(onClick = onBackClick, modifier = Modifier.testTag("CancelButton")) {
                    Text(stringResource(Res.string.cancel))
                }
            } else {
                TextButton(onClick = onBackClick, modifier = Modifier.testTag("BackButton")) {
                    Text(stringResource(Res.string.bookshelf_edit_label_back))
                }
            }
        },
        actionButton = {
            when (backStack.lastOrNull()) {
                is BookshelfEditPage.WizardEdit,
                is BookshelfEditPage.Discard,
                -> {
                    TextButton(
                        enabled = uiState.canSubmit,
                        onClick = onSaveClick,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text(stringResource(Res.string.bookshelf_edit_label_save))
                    }
                }

                BookshelfEditPage.WizardSelection -> Unit

                null -> Unit
            }
        },
        confirmButton = {
            when (backStack.lastOrNull()) {
                is BookshelfEditPage.WizardEdit,
                is BookshelfEditPage.Discard,
                -> {
                    FilledTonalButton(
                        enabled = uiState.canSubmit,
                        onClick = onSaveClick,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text(stringResource(Res.string.bookshelf_edit_label_save))
                    }
                }

                BookshelfEditPage.WizardSelection -> Unit

                null -> Unit
            }
        },
    ) { contentPadding ->
        content(contentPadding)
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
            uiState = BookshelfEditScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            backStack = remember {
                mutableStateListOf(BookshelfEditPage.WizardSelection)
            },
            onBack = {},
            onComplete = {},
            onTypeClick = {},
            updateCanSubmit = {},
            discardConfirm = {},
            onConfirm = {},
            onDismissRequest = {},
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
            uiState = BookshelfEditScreenUiState(
                title = stringResource(Res.string.bookshelf_wizard_title_register),
            ),
            backStack = remember {
                mutableStateListOf(
                    BookshelfEditPage.WizardSelection,
                    BookshelfEditPage.WizardEdit(BookshelfEditType.Register(BookshelfType.SMB)),
                )
            },
            onDismissRequest = {},
            onBackClick = {},
            onSaveClick = {},
        ) { contentPadding ->
            val scrollState = rememberScrollState()
            SmbEditorContents(
                form = rememberForm(
                    initialValue = SmbEditorForm(auth = SmbEditorForm.Auth.UserPass),
                ) {},
                uiState = BookshelfEditorScreenUiState(progress = false),
                modifier = Modifier.fillMaxHeight()
                    .drawDivider(scrollState, visibleTop = true, visibleBottom = true)
                    .verticalScroll(scrollState)
                    .padding(
                        contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                            .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                    ),
            )
        }
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
            uiState = BookshelfEditScreenUiState(
                title = "Register",
            ),
            backStack = remember {
                mutableStateListOf(
                    BookshelfEditPage.WizardSelection,
                    BookshelfEditPage.WizardEdit(BookshelfEditType.Register(BookshelfType.DEVICE)),
                )
            },
            onDismissRequest = {},
            onBackClick = {},
            onSaveClick = {},
        ) { contentPadding ->
            val scrollState = rememberScrollState()
            val form = rememberForm(initialValue = DeviceEditorForm()) {}
            DeviceEditorContents(
                form = form,
                folderSelectFieldState = rememberFolderSelectFieldState(
                    form = form,
                    onOpenDocumentTreeCancel = {},
                ),
                uiState = BookshelfEditorScreenUiState(progress = false),
                modifier = Modifier.fillMaxHeight()
                    .drawDivider(scrollState, visibleTop = true, visibleBottom = true)
                    .verticalScroll(scrollState)
                    .padding(
                        contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                            .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                    ),
            )
        }
    }
}
