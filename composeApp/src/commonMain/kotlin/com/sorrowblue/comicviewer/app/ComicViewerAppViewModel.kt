package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ComicViewerAppViewModel(
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
) : ViewModel()
