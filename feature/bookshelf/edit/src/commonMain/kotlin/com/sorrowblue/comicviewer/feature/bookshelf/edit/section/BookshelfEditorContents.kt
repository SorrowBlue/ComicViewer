/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkAccessPermissionDialog
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus

@Composable
internal fun BookshelfEditorContents(
    state: BookshelfEditScreenState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    when (state) {
        is LocalEditScreenState -> DeviceEditorContents(
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

        is SmbEditScreenState -> SmbEditorContents(
            form = state.form,
            uiState = state.uiState,
            modifier = modifier
                .drawDivider(scrollState, visibleTop = true, visibleBottom = true)
                .verticalScroll(scrollState)
                .padding(
                    contentPadding.only(PaddingValuesSides.Top + PaddingValuesSides.Horizontal)
                        .plus(PaddingValues(bottom = ComicTheme.dimension.padding)),
                )
        )
    }
    val currentOnChange by rememberUpdatedState(onChange)
    LaunchedEffect(state.formState.value) {
        currentOnChange(state.initialForm == state.formState.value)
    }
    EventEffect(state.events) {
        when (it) {
            BookshelfEditScreenEvent.Complete -> {
                onComplete()
            }
        }
    }

    if (state is SmbEditScreenState) {
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
