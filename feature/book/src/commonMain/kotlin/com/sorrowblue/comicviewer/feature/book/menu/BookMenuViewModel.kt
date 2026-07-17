/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class BookMenuViewModel(
    private val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel() {

    val bookSettingsFlow =
        manageBookSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun updateBookSettings(settings: BookSettings) {
        viewModelScope.launch {
            manageBookSettingsUseCase.edit { settings }
        }
    }
}
