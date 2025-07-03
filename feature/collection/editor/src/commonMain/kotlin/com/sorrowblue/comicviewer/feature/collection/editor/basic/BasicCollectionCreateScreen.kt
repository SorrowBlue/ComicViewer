package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.CollectionEditorFormData
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_create
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.compose.rememberForm

@Serializable
internal data class BasicCollectionCreate(
    val bookshelfId: BookshelfId = BookshelfId(),
    val path: String = "",
)

@Destination<BasicCollectionCreate>(style = DestinationStyle.Dialog::class)
@Composable
internal fun BasicCollectionsCreateScreen(
    route: BasicCollectionCreate,
    navController: NavController = koinInject(),
) {
    val state = rememberBasicCollectionCreateScreenState(route)
    FavoriteCreateScreen(
        uiState = state.uiState,
        onCancel = navController::popBackStack,
        onSubmit = state::onSubmit
    )

    EventEffect(state.event) {
        when (it) {
            BasicCollectionCreateScreenStateEvent.CreateComplete -> navController.popBackStack()
        }
    }
}

@Serializable
internal data class BasicCollectionsCreateScreenUiState(
    val formData: BasicCollectionEditorFormData = BasicCollectionEditorFormData(),
)

@Serializable
internal data class BasicCollectionEditorFormData(
    override val name: String = "",
) : CollectionEditorFormData {

    override fun <T : CollectionEditorFormData> update(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(name = name) as T
    }
}

@Composable
private fun FavoriteCreateScreen(
    uiState: BasicCollectionsCreateScreenUiState,
    onCancel: () -> Unit,
    onSubmit: (BasicCollectionEditorFormData) -> Unit,
) {
    val form = rememberForm(
        initialValue = uiState.formData,
        saver = kSerializableSaver<BasicCollectionEditorFormData>(),
        onSubmit = onSubmit
    )
    AlertDialog(
        title = { Text(text = stringResource(Res.string.collection_editor_title_basic_create)) },
        text = {
            CollectionNameTextField(form = form)
        },
        onDismissRequest = onCancel,
        confirmButton = {
            CreateButton(form = form)
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(Res.string.collection_editor_label_cancel))
            }
        }
    )
}
