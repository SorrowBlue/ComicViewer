/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class SecuritySettingsViewModel(
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
) : ViewModel() {

    val settingsFlow = manageSecuritySettingsUseCase.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, SecuritySettings())

    fun updateSettings(action: (SecuritySettings) -> SecuritySettings) {
        viewModelScope.launch {
            manageSecuritySettingsUseCase.edit(action)
        }
    }
}
