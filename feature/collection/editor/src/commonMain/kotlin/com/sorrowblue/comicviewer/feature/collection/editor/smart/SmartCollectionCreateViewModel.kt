/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class SmartCollectionCreateViewModel(
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
) : ViewModel() {

    val event: SharedFlow<SmartCollectionCreateViewModelEvent>
        field = MutableSharedFlow()

    val bookshelfListFlow = flowBookshelfListUseCase(EmptyRequest).map { it.dataOrNull() }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    fun onSubmit(formData: SmartCollectionForm) {
        viewModelScope.launch {
            createCollectionUseCase(
                CreateCollectionUseCase.Request(
                    SmartCollection(
                        formData.name,
                        formData.bookshelfId,
                        formData.searchCondition,
                    ),
                ),
            )
            event.emit(SmartCollectionCreateViewModelEvent.Complete)
        }
    }
}

internal sealed interface SmartCollectionCreateViewModelEvent {
    data object Complete : SmartCollectionCreateViewModelEvent
}
