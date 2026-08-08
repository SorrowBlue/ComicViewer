/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class ApplicationViewModel(
    initializer: Set<Initializer<*>>,
    manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
) : ViewModel() {

    init {
        Initializer.initialize(initializer.toList())
    }

    val displaySettings =
        manageDisplaySettingsUseCase.settings.map { it.darkMode }.distinctUntilChanged()
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
