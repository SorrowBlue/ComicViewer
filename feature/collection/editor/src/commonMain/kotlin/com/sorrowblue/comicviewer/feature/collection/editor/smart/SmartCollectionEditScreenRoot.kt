package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_smart_edit
import org.jetbrains.compose.resources.stringResource

@Composable
context(context: SmartCollectionEditScreenContext)
fun SmartCollectionEditScreenRoot(
    collectionId: CollectionId,
    onCancelClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberSmartCollectionEditScreenState(collectionId = collectionId)
    SmartCollectionEditorScreen(
        form = state.form,
        uiState = state.uiState,
        title = { Text(text = stringResource(Res.string.collection_editor_title_smart_edit)) },
        onCancel = onCancelClick,
        modifier = modifier,
    )
    EventEffect(state.event) {
        when (it) {
            SmartCollectionEditorScreenStateEvent.Complete -> onComplete()
        }
    }
}
