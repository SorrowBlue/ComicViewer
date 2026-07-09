/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.permission.localnetwork

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.AdaptiveAlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.framework.permission.generated.resources.Res
import comicviewer.framework.permission.generated.resources.permission_localnetwork_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocalNetworkAccessPermissionScreen(
    isRationale: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleText = stringResource(Res.string.permission_localnetwork_title)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(titleText)
                },
                navigationIcon = {
                    CloseIconButton(onClick = onDismissClick)
                },
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                ),
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        LocalNetworkAccessPermissionContent(
            isRationale = isRationale,
            onConfirmClick = onConfirmClick,
            onDismissClick = onDismissClick,
            modifier = Modifier.fillMaxWidth()
                .padding(contentPadding)
                .padding(ComicTheme.dimension.margin),
        )
    }
}

@Composable
fun LocalNetworkAccessPermissionDialog(
    isRationale: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFullScreenDialog = isCompactWindowClass()
    val titleText = stringResource(Res.string.permission_localnetwork_title)
    AdaptiveAlertDialog(
        title = {
            Text(titleText)
        },
        onBackClick = onDismissClick,
        isFullScreenDialog = isFullScreenDialog,
        navigationIcon = {
            CloseIconButton(onClick = onDismissClick)
        },
        modifier = modifier,
    ) { contentPadding ->
        LocalNetworkAccessPermissionContent(
            isRationale = isRationale,
            onConfirmClick = onConfirmClick,
            onDismissClick = onDismissClick,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Preview(device = Devices.PIXEL_9)
@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun LocalNetworkAccessPermissionScreenPreview() = PreviewTheme {
    LocalNetworkAccessPermissionScreen(
        isRationale = true,
        onDismissClick = {},
        onConfirmClick = {},
    )
}

@Preview(device = Devices.PIXEL_9)
@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun LocalNetworkAccessPermissionDialogPreview() = PreviewTheme {
    Box(Modifier.fillMaxSize())
    LocalNetworkAccessPermissionDialog(
        isRationale = true,
        onDismissClick = {},
        onConfirmClick = {},
    )
}
