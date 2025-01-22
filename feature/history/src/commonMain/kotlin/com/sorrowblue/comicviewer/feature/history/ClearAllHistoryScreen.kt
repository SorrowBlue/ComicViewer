package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import com.sorrowblue.comicviewer.framework.navigation.NavResultSender
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_btn_clear_all
import comicviewer.feature.history.generated.resources.history_text_clear_all
import comicviewer.feature.history.generated.resources.history_title_clear_all
import comicviewer.framework.ui.generated.resources.cancel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import comicviewer.framework.ui.generated.resources.Res as FrameworkUiRes

@Serializable
internal data object ClearAllHistory

@Destination<ClearAllHistory>(style = DestinationStyle.Dialog::class)
@Composable
internal fun ClearAllHistoryScreen(navResultSender: NavResultSender<Boolean>) {
    ClearAllHistoryScreen(
        onDismissRequest = navResultSender::navigateBack,
        onConfirm = { navResultSender.navigateBack(true) }
    )
}

@Composable
internal fun ClearAllHistoryScreen(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.history_btn_clear_all))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(FrameworkUiRes.string.cancel))
            }
        },
        title = { Text(text = stringResource(Res.string.history_title_clear_all)) },
        text = { Text(text = stringResource(Res.string.history_text_clear_all)) }
    )
}
