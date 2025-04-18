package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorForm
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import kotlinx.serialization.Serializable

@Serializable
internal data class SmartCollectionEditorScreenUiState(
    val formData: SmartCollectionEditorFormData = SmartCollectionEditorFormData(),
    val bookshelfList: List<Bookshelf> = emptyList(),
)

@Composable
internal fun SmartCollectionEditorScreen(
    state: SmartCollectionEditorScreenState,
    title: @Composable () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
) {
    SmartCollectionEditorScreen(
        uiState = state.uiState,
        title = title,
        onCancel = onCancel,
        onSubmit = state::onSubmit,
    )
    EventEffect(state.event) {
        when (it) {
            SmartCollectionEditorScreenStateEvent.Complete -> onComplete()
        }
    }
}

@Composable
private fun SmartCollectionEditorScreen(
    uiState: SmartCollectionEditorScreenUiState,
    title: @Composable () -> Unit,
    onCancel: () -> Unit,
    onSubmit: suspend (SmartCollectionEditorFormData) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = title
    ) { contentPadding ->
        SmartCollectionEditorForm(
            formData = uiState.formData,
            bookshelfList = uiState.bookshelfList,
            onCancel = onCancel,
            onSubmit = onSubmit,
            modifier = Modifier.padding(contentPadding)
        )
    }
}
