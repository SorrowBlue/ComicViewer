package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.lifecycle.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import logcat.logcat

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
internal class ComicViewerAppViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val splitInstallManager: SplitInstallManager,
    private val securitySettingsUseCase: ManageSecuritySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
) : ComposeViewModel<ComicViewerAppUiEvent>() {

    private var isRestart by savedStateHandle.saveable { mutableStateOf(false) }

    var shouldKeepSplash = true

    val addOnList = MutableStateFlow(
        splitInstallManager.installedModules
            .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
            .toPersistentList()
    )

    private var isRestoredNavHistory = false

    fun onCreate() {
        if (!isRestart) {
            viewModelScope.launch {
                val settings = loadSettingsUseCase.settings.first()
                if (!settings.doneTutorial) {
                    updateUiEvent(
                        ComicViewerAppUiEvent.StartTutorial {
                            shouldKeepSplash = false
                            isRestart = true
                        }
                    )
                } else if (manageDisplaySettingsUseCase.settings.first().restoreOnLaunch) {
                    val history =
                        getNavigationHistoryUseCase.execute(EmptyRequest).map { it.dataOrNull }
                            .first()
                    if (history?.triple?.second?.isNotEmpty() == true) {
                        isRestoredNavHistory = true
                        updateUiEvent(ComicViewerAppUiEvent.RestoreHistory(history))
                    } else {
                        completeRestoreHistory()
                    }
                } else {
                    completeRestoreHistory()
                }
            }
        }
    }

    fun onStart() {
        addOnList.value =
            splitInstallManager.installedModules
                .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
                .toPersistentList()
        if (isRestart) {
            viewModelScope.launch {
                val securitySettings = securitySettingsUseCase.settings.first()
                if (securitySettings.lockOnBackground && securitySettings.password != null) {
                    logcat { "認証" }
                    updateUiEvent(
                        ComicViewerAppUiEvent.RequireAuthentication(true) {
                            shouldKeepSplash = false
                        }
                    )
                } else {
                    shouldKeepSplash = false
                }
            }
        }
    }

    fun onCompleteTutorial() {
        viewModelScope.launch {
            val doneTutorial = loadSettingsUseCase.settings.first().doneTutorial
            if (!doneTutorial) {
                loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
            }
            updateUiEvent(ComicViewerAppUiEvent.CompleteTutorial(isFirstTime = !doneTutorial))
        }
    }

    fun completeRestoreHistory() {
        viewModelScope.launch {
            if (securitySettingsUseCase.settings.first().password != null) {
                logcat { "認証 復元完了後" }
                updateUiEvent(
                    ComicViewerAppUiEvent.RequireAuthentication(isRestoredNavHistory) {
                        shouldKeepSplash = false
                        isRestart = true
                    }
                )
            } else {
                shouldKeepSplash = false
                isRestart = true
            }
        }
    }
}
