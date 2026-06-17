package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CollectionNameTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_create
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Serializable
internal data class BasicCollectionsCreateScreenUiState(val isLoading: Boolean = false)

@Serializable
internal data class BasicCollectionForm(val name: String = "")

@NavDestination(BasicCollectionCreateNavKey::class)
@Composable
internal fun BasicCollectionCreateScreen(
    uiState: BasicCollectionsCreateScreenUiState,
    form: Form<BasicCollectionForm>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        title = { Text(text = stringResource(Res.string.collection_editor_title_basic_create)) },
        text = {
            CollectionNameTextField(form = form, enabled = !uiState.isLoading)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            CreateButton(
                isLoading = uiState.isLoading,
                form = form,
                modifier = Modifier.testTag("CreateButton"),
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !uiState.isLoading,
                modifier = Modifier.testTag("CloseButton"),
            ) {
                Text(text = stringResource(Res.string.collection_editor_label_cancel))
            }
        },
        modifier = modifier,
    )
}

@NavPreview(BasicCollectionCreateNavKey::class, primary = true)
@Preview
@Composable
private fun BasicCollectionCreateScreenPreview() = PreviewTheme {
    val formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    val form = rememberForm(state = formState, onSubmit = {})
    BasicCollectionCreateScreen(
        uiState = BasicCollectionsCreateScreenUiState(),
        form = form,
        onDismissRequest = {},
    )
}
