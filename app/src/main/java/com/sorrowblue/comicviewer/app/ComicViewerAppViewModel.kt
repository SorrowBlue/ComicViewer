package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

@HiltViewModel
internal class ComicViewerAppViewModel @Inject constructor(
    private val getInstalledModulesUseCase: GetInstalledModulesUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val securitySettingsUseCase: ManageSecuritySettingsUseCase,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
) : ViewModel() {

    var shouldKeepSplash = true

    suspend fun isTutorial() = !loadSettingsUseCase.settings.first().doneTutorial
    suspend fun isNeedToRestore() = manageDisplaySettingsUseCase.settings.first().restoreOnLaunch

    suspend fun history() =
        getNavigationHistoryUseCase.execute(EmptyRequest).first().fold({ it }, { null })

    suspend fun isAuth() = securitySettingsUseCase.settings.first().password != null
    suspend fun lockOnBackground() = securitySettingsUseCase.settings.first().let {
        it.lockOnBackground && it.password != null
    }

    suspend fun onTutorialComplete() {
        loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
    }

    val installedModules: Flow<Set<String>>
        get() =
            getInstalledModulesUseCase.execute(GetInstalledModulesUseCase.Request)
                .mapNotNull { it.dataOrNull }
}
