package com.sorrowblue.comicviewer.feature.collection.editor.smart

import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import com.sorrowblue.comicviewer.framework.ui.EventFlow

internal interface SmartCollectionEditorScreenState {

    val uiState: SmartCollectionEditorScreenUiState

    val event: EventFlow<SmartCollectionEditorScreenStateEvent>

    fun onSubmit(formData: SmartCollectionEditorFormData)
}

internal sealed interface SmartCollectionEditorScreenStateEvent {

    data object Complete : SmartCollectionEditorScreenStateEvent
}
