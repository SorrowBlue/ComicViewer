/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class TutorialViewModel(val manageViewerSettingsUseCase: ManageViewerSettingsUseCase) :
    ViewModel() {

    val bindingDirection = manageViewerSettingsUseCase.settings
        .map { it.bindingDirection }.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    fun updateBindingDirection(bindingDirection: BindingDirection) {
        viewModelScope.launch {
            manageViewerSettingsUseCase.edit {
                it.copy(bindingDirection = bindingDirection)
            }
        }
    }
}
