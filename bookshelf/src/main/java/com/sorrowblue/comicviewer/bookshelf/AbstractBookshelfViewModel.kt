package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

abstract class AbstractBookshelfViewModel(
    manageBookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase
) : ViewModel() {

    abstract val transitionName: String?
    abstract val pagingDataFlow: Flow<PagingData<File>>
    abstract val pagingQueryDataFlow: Flow<PagingData<File>>
    abstract var position: Int
    abstract val titleFlow: StateFlow<String>
    abstract val subTitleFlow: StateFlow<String>

    var isInitialize = false

    val isRefreshing = MutableStateFlow(false)
    val isEmptyData = MutableStateFlow(false)

    val bookshelfDisplaySettingsFlow = manageBookshelfDisplaySettingsUseCase.settings
    open var query = ""

    val spanCountFlow = bookshelfDisplaySettingsFlow.map {
        when (it.display) {
            BookshelfDisplaySettings.Display.GRID -> it.spanCount
            BookshelfDisplaySettings.Display.LIST -> 1
        }
    }
}
