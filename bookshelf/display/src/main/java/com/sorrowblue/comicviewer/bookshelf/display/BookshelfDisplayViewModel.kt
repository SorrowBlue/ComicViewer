package com.sorrowblue.comicviewer.bookshelf.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfDisplayViewModel @Inject constructor(
    private val settingsUseCase: ManageBookshelfDisplaySettingsUseCase,
) : ViewModel() {

    fun update(display: BookshelfDisplaySettings.Display) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(display = display) }
        }
    }

    fun update(spanCount: Int) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(spanCount = spanCount) }
        }
    }

    fun update(sort: BookshelfDisplaySettings.Sort) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(sort = sort) }
        }
    }

    fun update(order: BookshelfDisplaySettings.Order) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(order = order) }
        }
    }

    val displayFlow = settingsUseCase.settings.map { it.display }

    val spanCountFlow = settingsUseCase.settings.map { it.spanCount }

    val sortFlow = settingsUseCase.settings.map { it.sort }

    val orderFlow = settingsUseCase.settings.map { it.order }
}
