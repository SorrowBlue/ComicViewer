/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display.darkmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class DarkModeViewModel(
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
) : ViewModel() {

    val settingsFlow =
        displaySettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun updateSettings(action: (DisplaySettings) -> DisplaySettings, done: () -> Unit) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                action(it)
            }
            done()
        }
    }
}
