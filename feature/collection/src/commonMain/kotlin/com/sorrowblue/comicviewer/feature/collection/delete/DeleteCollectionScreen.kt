package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_delete_confirm
import comicviewer.feature.collection.generated.resources.collection_title_delete
import comicviewer.framework.ui.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

data class DeleteCollectionScreenUiState(val name: String = "")

@Composable
internal fun DeleteCollectionScreen(
    uiState: DeleteCollectionScreenUiState,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onBackClick,
        title = {
            Text(stringResource(Res.string.collection_title_delete))
        },
        text = {
            Text(stringResource(Res.string.collection_label_delete_confirm, uiState.name))
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("ConfirmButton"),
            ) {
                Text(stringResource(Res.string.collection_label_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("DismissButton"),
            ) {
                Text(stringResource(FrameworkResString.cancel))
            }
        },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DeleteCollectionScreenPreview() {
    PreviewTheme {
        DeleteCollectionScreen(
            uiState = DeleteCollectionScreenUiState(
                name = fakeBasicCollection().name,
            ),
            onBackClick = {},
            onConfirm = {},
        )
    }
}
