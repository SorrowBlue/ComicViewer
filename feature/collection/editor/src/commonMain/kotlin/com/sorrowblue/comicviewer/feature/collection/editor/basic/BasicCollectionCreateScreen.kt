package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_create
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Serializable
internal data class BasicCollectionsCreateScreenUiState(
    val isLoading: Boolean = false,
)

@Serializable
internal data class BasicCollectionForm(val name: String = "")

@Composable
internal fun BasicCollectionCreateScreen(
    uiState: BasicCollectionsCreateScreenUiState,
    form: Form<BasicCollectionForm>,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = stringResource(Res.string.collection_editor_title_basic_create)) },
        text = {
            CollectionNameTextField(form = form, enabled = !uiState.isLoading)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            CreateButton(isLoading = uiState.isLoading, form = form)
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !uiState.isLoading
            ) {
                Text(text = stringResource(Res.string.collection_editor_label_cancel))
            }
        }
    )
}
