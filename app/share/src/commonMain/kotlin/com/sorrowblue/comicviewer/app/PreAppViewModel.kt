/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class PreAppViewModel(
    val loadSettingsUseCase: LoadSettingsUseCase,
    val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val tutorialRequired = loadSettingsUseCase.settings
        .mapLatest { !it.doneTutorial }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val authRequired = manageSecuritySettingsUseCase.settings
        .mapLatest { !it.password.isNullOrEmpty() }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val lockOnBackground = manageSecuritySettingsUseCase.settings
        .map { it.lockOnBackground }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)


    fun completeTutorial() {
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }
}
