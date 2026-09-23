/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.feature.permission.nav.LocalNetworkAccessPermissionNavKey
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.material3.AdaptiveAlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.permission.generated.resources.Res
import comicviewer.feature.permission.generated.resources.permission_localnetwork_title
import org.jetbrains.compose.resources.stringResource

/**
 * Dialog composable for requesting local network access permission.
 *
 * @param isRationale Whether the permission is in rationale state.
 * @param onConfirmClick Callback when confirm button is clicked.
 * @param onDismissClick Callback when dismiss/close button is clicked.
 * @param modifier Modifier to be applied to the dialog.
 */
@NavDestination(LocalNetworkAccessPermissionNavKey::class)
@Composable
internal fun LocalNetworkAccessPermissionScreen(
    isRationale: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleText = stringResource(Res.string.permission_localnetwork_title)
    val title = movableContentOf {
        Text(titleText)
    }
    val content = movableContentOf<PaddingValues> { contentPadding ->
        LocalNetworkAccessPermissionContent(
            isRationale = isRationale,
            onConfirmClick = onConfirmClick,
            onDismissClick = onDismissClick,
            modifier = Modifier.padding(contentPadding + PaddingValues(16.dp)),
        )
    }
    val isFullScreenDialog = isCompactWindowClass()
    if (isFullScreenDialog) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        CloseIconButton(onDismissClick)
                    },
                )
            },
        ) { contentPadding ->
            content(contentPadding)
        }
    } else {
        AdaptiveAlertDialog(
            title = title,
            onBackClick = onDismissClick,
            isFullScreenDialog = isFullScreenDialog,
            navigationIcon = {
                CloseIconButton(onClick = onDismissClick)
            },
            modifier = modifier,
        ) { contentPadding ->
            content(contentPadding)
        }
    }
}

@NavPreview(LocalNetworkAccessPermissionNavKey::class)
@Preview(device = Devices.PIXEL_9)
@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun LocalNetworkAccessPermissionScreenPreview() = PreviewTheme {
    Box(Modifier.fillMaxSize())
    LocalNetworkAccessPermissionScreen(
        isRationale = true,
        onDismissClick = {},
        onConfirmClick = {},
    )
}
