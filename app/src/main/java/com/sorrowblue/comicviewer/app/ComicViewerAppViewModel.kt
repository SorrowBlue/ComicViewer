package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ComicViewerAppViewModel @Inject constructor(
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    val getInstalledModulesUseCase: GetInstalledModulesUseCase,
) : ViewModel()
