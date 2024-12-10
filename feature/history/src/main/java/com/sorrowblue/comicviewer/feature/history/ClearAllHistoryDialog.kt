package com.sorrowblue.comicviewer.feature.history

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryGraph
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Destination<HistoryGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ClearAllHistoryDialog(
    resultNavigator: ResultBackNavigator<Boolean>,
) {
    ClearAllHistoryDialog(
        onDismissRequest = { resultNavigator.navigateBack() },
        onConfirm = { resultNavigator.navigateBack(true) }
    )
}

@Composable
private fun ClearAllHistoryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.history_btn_clear_all))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.history_title_clear_all))
        },
        text = {
            Text(text = stringResource(R.string.history_text_clear_all))
        }
    )
}

@PreviewLightDark
@Composable
private fun DeleteAllDialogPreview() {
    PreviewTheme {
        ClearAllHistoryDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
