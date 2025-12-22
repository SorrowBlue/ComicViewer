package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_smart_create
import org.jetbrains.compose.resources.stringResource

@Composable
context(context: SmartCollectionCreateScreenContext)
fun SmartCollectionCreateScreenRoot(
    bookshelfId: BookshelfId?,
    searchCondition: SearchCondition,
    onCancelClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberSmartCollectionCreateScreenState(bookshelfId, searchCondition)
    SmartCollectionEditorScreen(
        form = state.form,
        uiState = state.uiState,
        title = { Text(text = stringResource(Res.string.collection_editor_title_smart_create)) },
        onCancel = onCancelClick,
        modifier = modifier.testTag("SmartCollectionCreateScreenRoot"),
    )
    EventEffect(state.event) {
        when (it) {
            SmartCollectionEditorScreenStateEvent.Complete -> onComplete()
        }
    }
}
