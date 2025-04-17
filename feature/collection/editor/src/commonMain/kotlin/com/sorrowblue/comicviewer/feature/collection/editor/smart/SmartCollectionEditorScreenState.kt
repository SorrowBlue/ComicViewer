package com.sorrowblue.comicviewer.feature.collection.editor.smart

import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData

internal interface SmartCollectionEditorScreenState {

    val uiState: SmartCollectionEditorScreenUiState

    suspend fun onSubmit(formData: SmartCollectionEditorFormData)
}
