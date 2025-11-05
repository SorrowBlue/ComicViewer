package com.sorrowblue.comicviewer.feature.bookshelf.info.notification

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_btn_ok
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_text_scan_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_text_scan_thumbnail
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
enum class ScanType {
    File,
    Thumbnail,
}

@Composable
internal fun NotificationRequestScreen(
    scanType: ScanType,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text(text = stringResource(Res.string.bookshelf_info_notification_btn_ok))
            }
        },
        title = {
            Text(text = stringResource(Res.string.bookshelf_info_notification_title))
        },
        icon = {
            Icon(imageVector = ComicIcons.Notifications, contentDescription = null)
        },
        text = {
            val id = when (scanType) {
                ScanType.File -> Res.string.bookshelf_info_notification_text_scan_file
                ScanType.Thumbnail -> Res.string.bookshelf_info_notification_text_scan_thumbnail
            }
            Text(text = stringResource(id))
        }
    )
}

@Preview
@Composable
private fun NotificationRequestScreenPreview() {
    PreviewTheme {
        NotificationRequestScreen(
            scanType = ScanType.File,
            onDismissRequest = {},
            onConfirmClick = {},
        )
    }
}
