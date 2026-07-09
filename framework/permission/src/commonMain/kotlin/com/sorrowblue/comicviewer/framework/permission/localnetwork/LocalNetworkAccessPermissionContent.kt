/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.permission.localnetwork

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.LocalAccessNetwork
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ShieldLock
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import comicviewer.framework.permission.generated.resources.Res
import comicviewer.framework.permission.generated.resources.permission_localnetwork_action_cancel
import comicviewer.framework.permission.generated.resources.permission_localnetwork_action_continue
import comicviewer.framework.permission.generated.resources.permission_localnetwork_action_open_settings
import comicviewer.framework.permission.generated.resources.permission_localnetwork_desc
import comicviewer.framework.permission.generated.resources.permission_localnetwork_headline
import comicviewer.framework.permission.generated.resources.permission_localnetwork_privacy_desc
import comicviewer.framework.permission.generated.resources.permission_localnetwork_privacy_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocalNetworkAccessPermissionContent(
    isRationale: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val confirmText = stringResource(
            if (isRationale) {
                Res.string.permission_localnetwork_action_continue
            } else {
                Res.string.permission_localnetwork_action_open_settings
            },
        )
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            Image(
                ComicIcons.LocalAccessNetwork,
                null,
                modifier = Modifier.weight(1f).aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.size(16.dp))
            Text(
                stringResource(Res.string.permission_localnetwork_headline),
                style = ComicTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                stringResource(Res.string.permission_localnetwork_desc),
                style = ComicTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.size(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = ComicIcons.ShieldLock,
                        contentDescription = null,
                        tint = ComicTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.padding(8.dp))
                    Column {
                        Text(
                            stringResource(Res.string.permission_localnetwork_privacy_title),
                            style = ComicTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = ComicTheme.colorScheme.primary,
                        )
                        Text(
                            stringResource(Res.string.permission_localnetwork_privacy_desc),
                            style = ComicTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Spacer(Modifier.size(32.dp))

            val isFullScreenDialog = isCompactWindowClass()
            if (isFullScreenDialog) {
                Column {
                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                    ) {
                        Icon(ComicIcons.OpenInBrowser, null)
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(confirmText)
                    }
                    OutlinedButton(
                        onClick = onDismissClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.permission_localnetwork_action_cancel))
                    }
                }
            } else {
                Row {
                    OutlinedButton(
                        onClick = onDismissClick,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(Res.string.permission_localnetwork_action_cancel))
                    }
                    Spacer(Modifier.padding(ComicTheme.dimension.targetSpacing))
                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.weight(1f),
                        contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                    ) {
                        Icon(ComicIcons.OpenInBrowser, null)
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(confirmText)
                    }
                }
            }
        }
    }
}
