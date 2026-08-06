/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditPage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenEvent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.drawDivider
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkAccessPermissionDialog
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus

internal fun EntryProviderScope<NavKey>.bookshelfEditorEntry(
    eventFlow: EventFlow<BookshelfEditScreenEvent>,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    discardConfirm: (Boolean) -> Unit,
    updateCanSubmit: (Boolean) -> Unit,
    contentPadding: PaddingValues,
) {
    entry<BookshelfEditPage.WizardEdit>(
        metadata = metadata {
            transitionMaterialSharedAxisX()
        },
    ) { key ->
        val state = rememberBookshelfEditorScreenState(editType = key.editType)
        BookshelfEditorScreen(
            eventFlow = eventFlow,
            state = state,
            contentPadding = contentPadding,
            onBack = onBack,
            onComplete = onComplete,
            discardConfirm = discardConfirm,
            updateCanSubmit = updateCanSubmit,
            modifier = Modifier.testTag("BookshelfEditorScreen"),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BookshelfEditorScreen(
    eventFlow: EventFlow<BookshelfEditScreenEvent>,
    state: BookshelfEditorScreenState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    discardConfirm: (Boolean) -> Unit,
    updateCanSubmit: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None,
    )
    NavigationBackHandler(
        isBackEnabled = true,
        state = navigationState,
        onBackCancelled = {},
        onBackCompleted = {
            if (state.initialForm == state.formState.value) {
                onBack()
            } else {
                discardConfirm(false)
            }
        },
    )
    EventEffect(eventFlow) {
        when (it) {
            BookshelfEditScreenEvent.BackClick -> {
                if (state.initialForm == state.formState.value) {
                    onBack()
                } else {
                    discardConfirm(false)
                }
            }

            BookshelfEditScreenEvent.SubmitClick -> {
                state.form.handleSubmit()
            }

            BookshelfEditScreenEvent.DismissRequest -> {
                if (state.initialForm == state.formState.value) {
                    onComplete()
                } else {
                    discardConfirm(true)
                }
            }
        }
    }
    val scrollState = rememberScrollState()
    when (state) {
        is LocalEditorScreenState -> DeviceEditorContents(
            form = state.form,
            folderSelectFieldState = state.folderSelectFieldState,
            uiState = state.uiState,
            modifier = modifier
                .drawDivider(scrollState, visibleTop = true, visibleBottom = true)
                .verticalScroll(scrollState)
                .padding(
                    contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                        .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                ),
        )

        is SmbEditorScreenState -> SmbEditorContents(
            form = state.form,
            uiState = state.uiState,
            modifier = modifier
                .drawDivider(scrollState, visibleTop = true, visibleBottom = true)
                .verticalScroll(scrollState)
                .padding(
                    contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                        .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                ),
        )
    }
    val currentUpdateCanSubmit by rememberUpdatedState(updateCanSubmit)
    LaunchedEffect(state.formState.value) {
        currentUpdateCanSubmit(state.formState.meta.canSubmit)
    }
    EventEffect(state.events) {
        when (it) {
            BookshelfEditorScreenEvent.Complete -> {
                onComplete()
            }
        }
    }

    if (state is SmbEditorScreenState) {
        val permissionState = state.permissionRequester.state
        if (
            permissionState is LocalNetworkPermissionState.Rationale ||
            permissionState is LocalNetworkPermissionState.DeniedPermanent
        ) {
            LocalNetworkAccessPermissionDialog(
                isRationale = permissionState is LocalNetworkPermissionState.Rationale,
                onConfirmClick = state::onPermissionConfirmClick,
                onDismissClick = onBack,
            )
        }
    }
}
