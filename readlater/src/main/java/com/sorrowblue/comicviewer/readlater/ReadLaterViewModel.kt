package com.sorrowblue.comicviewer.readlater

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    bookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
    pagingReadLaterUseCase: PagingReadLaterUseCase,
) : PagingViewModel<File>() {

    override val transitionName = null
    override val pagingDataFlow = pagingReadLaterUseCase
        .execute(PagingReadLaterUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)

    val bookshelfDisplaySettingsFlow = bookshelfDisplaySettingsUseCase.settings
    val spanCountFlow = bookshelfDisplaySettingsFlow.map { it.rawSpanCount }
        .stateIn { runBlocking { bookshelfDisplaySettingsFlow.first().rawSpanCount } }

    private val BookshelfDisplaySettings.rawSpanCount
        get() = when (display) {
            BookshelfDisplaySettings.Display.GRID -> spanCount
            BookshelfDisplaySettings.Display.LIST -> 1
        }
}
