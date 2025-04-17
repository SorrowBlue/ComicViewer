package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultSender
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_btn_cancel
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_btn_not_allowed
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_btn_ok
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_text_scan_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_text_scan_thumbnail
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
enum class NotificationRequestResult {
    Ok,
    Cancel,
    NotAllowed,
}

@Serializable
enum class ScanType {
    File,
    Thumbnail,
}

@Serializable
data class NotificationRequest(val scanType: ScanType)

@Destination<NotificationRequest>(style = DestinationStyle.Dialog::class)
@Composable
internal fun NotificationRequestScreen(
    route: NotificationRequest,
    resultNavigator: NavResultSender<NotificationRequestResult>,
) {
    NotificationRequestScreen(
        type = route.scanType,
        onDismissRequest = { resultNavigator.navigateBack(result = NotificationRequestResult.Cancel) },
        onConfirmClick = { resultNavigator.navigateBack(result = NotificationRequestResult.Ok) },
        onNotAllowedClick = { resultNavigator.navigateBack(result = NotificationRequestResult.NotAllowed) },
        onCancelClick = { resultNavigator.navigateBack(result = NotificationRequestResult.Cancel) }
    )
}

@Composable
internal fun NotificationRequestScreen(
    type: ScanType,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onNotAllowedClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Row {
                TextButton(onClick = onCancelClick) {
                    Text(text = stringResource(Res.string.bookshelf_info_notification_btn_cancel))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onNotAllowedClick) {
                    Text(text = stringResource(Res.string.bookshelf_info_notification_btn_not_allowed))
                }
                Spacer(modifier = Modifier.size(ComicTheme.dimension.targetSpacing))
                Button(onClick = onConfirmClick) {
                    Text(text = stringResource(Res.string.bookshelf_info_notification_btn_ok))
                }
            }
        },
        title = {
            Text(text = stringResource(Res.string.bookshelf_info_notification_title))
        },
        icon = {
            Icon(imageVector = ComicIcons.Notifications, contentDescription = null)
        },
        text = {
            val id = when (type) {
                ScanType.File -> Res.string.bookshelf_info_notification_text_scan_file
                ScanType.Thumbnail -> Res.string.bookshelf_info_notification_text_scan_thumbnail
            }
            Text(text = stringResource(id))
        }
    )
}
