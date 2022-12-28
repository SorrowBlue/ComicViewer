package com.sorrowblue.comicviewer.bookshelf.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfDisplayViewModel @Inject constructor(
    private val settingsUseCase: ManageBookshelfDisplaySettingsUseCase,
) : ViewModel() {

    val displayFlow = MutableStateFlow(BookshelfDisplaySettings.DEFAULT_DISPLAY).apply {
        shareIn(viewModelScope, SharingStarted.WhileSubscribed()).onEach { display ->
            settingsUseCase.edit { it.copy(display = display) }
        }.launchIn(viewModelScope)
    }

    val spanCountFlow = MutableStateFlow(BookshelfDisplaySettings.DEFAULT_SPAN_COUNT).apply {
        shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .onEach { spanCount ->
                settingsUseCase.edit { it.copy(spanCount = spanCount) }
            }.launchIn(viewModelScope)
    }

    val sortFlow = MutableStateFlow(BookshelfDisplaySettings.DEFAULT_SORT).apply {
        shareIn(viewModelScope, SharingStarted.WhileSubscribed()).onEach { sort ->
            settingsUseCase.edit { it.copy(sort = sort) }
        }.launchIn(viewModelScope)
    }

    val orderFlow = MutableStateFlow(BookshelfDisplaySettings.DEFAULT_ORDER).apply {
        shareIn(viewModelScope, SharingStarted.WhileSubscribed()).onEach { order ->
            settingsUseCase.edit { it.copy(order = order) }
        }.launchIn(viewModelScope)
    }

    init {
        viewModelScope.launch {
            settingsUseCase.settings.first().apply {
                displayFlow.value = display
                spanCountFlow.value = spanCount
                sortFlow.value = sort
                orderFlow.value = order
            }
        }
    }
}
