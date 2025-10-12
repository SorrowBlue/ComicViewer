package com.sorrowblue.comicviewer.feature.collection.editor.smart

import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import soil.form.compose.Form

internal interface SmartCollectionEditorScreenState {

    val uiState: SmartCollectionEditorScreenUiState
    val form: Form<SmartCollectionForm>

    val event: EventFlow<SmartCollectionEditorScreenStateEvent>

    fun onSubmit(formData: SmartCollectionForm)
}

internal sealed interface SmartCollectionEditorScreenStateEvent {

    data object Complete : SmartCollectionEditorScreenStateEvent
}
