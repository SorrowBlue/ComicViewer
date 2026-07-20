/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@AssistedInject
internal class ReceiveBookViewModel(
    @Assisted uri: String?,
    getIntentBookUseCase: GetIntentBookUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    val viewerSettingsFlow = manageViewerSettingsUseCase.settings
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val bookFlow = if (uri == null) {
        flowOf(null)
    } else {
        getIntentBookUseCase(GetIntentBookUseCase.Request(uri)).map { it.dataOrNull() }
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(uri: String?): ReceiveBookViewModel
    }
}
