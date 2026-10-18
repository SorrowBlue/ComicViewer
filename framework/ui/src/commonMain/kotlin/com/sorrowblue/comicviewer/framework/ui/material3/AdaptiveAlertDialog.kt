/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

internal val DialogMinWidth = 280.dp
internal val DialogMaxWidth = 560.dp

@Composable
fun AdaptiveAlertDialog(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFullScreenDialog: Boolean = isCompactWindowClass(),
    navigationIcon: @Composable () -> Unit = { BackIconButton(onClick = onBackClick) },
    content: @Composable ((PaddingValues) -> Unit),
) {
    val movableContent = remember {
        movableContentOf { contentPadding: PaddingValues -> content(contentPadding) }
    }
    AdaptiveBasicAlertDialog(
        onDismissRequest = onBackClick,
        isFullScreenDialog = isFullScreenDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier,
    ) {
        if (isFullScreenDialog) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = title,
                        navigationIcon = navigationIcon,
                        windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                content = movableContent,
            )
        } else {
            AlertDialogContent(
                title = title,
                content = {
                    movableContent(it)
                },
            )
        }
    }
}

@Composable
fun AdaptiveAlertDialog(
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    actionButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    isFullScreenDialog: Boolean = isCompactWindowClass(),
    navigationIcon: @Composable () -> Unit = {},
    content: @Composable ((PaddingValues) -> Unit),
) {
    val movableContent = remember {
        movableContentOf { contentPadding: PaddingValues -> content(contentPadding) }
    }
    AdaptiveBasicAlertDialog(
        onDismissRequest = onDismissRequest,
        isFullScreenDialog = isFullScreenDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier,
    ) {
        if (isFullScreenDialog) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = title,
                        navigationIcon = navigationIcon,
                        actions = {
                            actionButton()
                        },
                        windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                content = movableContent,
            )
        } else {
            AlertDialogContent(
                title = title,
                confirmButton = confirmButton,
                dismissButton = dismissButton,
                content = {
                    movableContent(it)
                },
            )
        }
    }
}

@Composable
private fun AdaptiveBasicAlertDialog(
    onDismissRequest: () -> Unit,
    isFullScreenDialog: Boolean,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Box(
            modifier =
                modifier.then(
                    if (isFullScreenDialog) {
                        Modifier
                    } else {
                        Modifier.sizeIn(
                            minWidth = DialogMinWidth,
                            maxWidth = DialogMaxWidth,
                        ).sizeIn(maxHeight = DialogMaxWidth)
                    },
                ),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}

private val ContentWindowInsets
    @ReadOnlyComposable
    @Composable
    get() = ComicTheme.dimension.run {
        WindowInsets(left = margin, right = margin, bottom = margin)
    }

private val AppBarWindowInsets get() = WindowInsetsSides.Horizontal + WindowInsetsSides.Top
