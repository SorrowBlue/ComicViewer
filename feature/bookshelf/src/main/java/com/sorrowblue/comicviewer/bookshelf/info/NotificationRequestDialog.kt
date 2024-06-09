package com.sorrowblue.comicviewer.bookshelf.info

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
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

enum class NotificationRequestResult {
    Ok,
    Cancel,
    NotAllowed,
}

@Destination<BookshelfGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun NotificationRequestDialog(
    resultNavigator: ResultBackNavigator<NotificationRequestResult>,
) {
    NotificationRequestDialog(
        onDismissRequest = { resultNavigator.navigateBack(result = NotificationRequestResult.Cancel) },
        onConfirmClick = { resultNavigator.navigateBack(result = NotificationRequestResult.Ok) },
        onNotAllowedClick = { resultNavigator.navigateBack(result = NotificationRequestResult.NotAllowed) },
        onCancelClick = { resultNavigator.navigateBack(result = NotificationRequestResult.Cancel) }
    )
}

@Composable
private fun NotificationRequestDialog(
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
                    Text(text = stringResource(id = android.R.string.cancel))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onNotAllowedClick) {
                    Text(text = "Not allowed")
                }
                Spacer(modifier = Modifier.size(ComicTheme.dimension.targetSpacing))
                Button(onClick = onConfirmClick) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        },
        title = {
            Text(text = "通知許可")
        },
        icon = {
            Icon(imageVector = ComicIcons.Notifications, contentDescription = null)
        },
        text = {
            Text(text = "本棚のスキャンの進行状況を通知で確認できます。")
        }
    )
}

@PreviewMultiScreen
@Composable
private fun PreviewNotificationRequestDialog() {
    PreviewTheme {
        NotificationRequestDialog(
            onDismissRequest = {},
            onConfirmClick = {},
            onNotAllowedClick = {},
            onCancelClick = {}
        )
    }
}
