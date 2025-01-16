package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import org.koin.android.annotation.KoinViewModel


/**
 * Comic viewer app view model
 *
 * @property getNavigationHistoryUseCase
 * @property manageDisplaySettingsUseCase
 * @property getInstalledModulesUseCase
 * @constructor Create empty Comic viewer app view model
 */
@KoinViewModel
internal class ComicViewerAppViewModel(
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    val getInstalledModulesUseCase: GetInstalledModulesUseCase,
) : ViewModel()
