package com.sorrowblue.comicviewer.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.AddOn
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class ComicViewerAppViewModel @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiEvents =
        MutableSharedFlow<ComicViewerAppUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvents = _uiEvents.asSharedFlow()

    var shouldKeepOnScreen = true

    val history = MutableStateFlow<NavigationHistory?>(null)

    val addOnList = MutableStateFlow(
        splitInstallManager.installedModules
            .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
            .toPersistentList()
    )

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        addOnList.value =
            splitInstallManager.installedModules
                .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
                .toPersistentList()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch {
            val settings = loadSettingsUseCase.settings.first()
            if (!settings.doneTutorial) {
                _uiEvents.emit(ComicViewerAppUiEvent.StartTutorial)
                shouldKeepOnScreen = false
            } else if (settings.restoreOnLaunch) {
                val his =
                    getNavigationHistoryUseCase.execute(EmptyRequest).map { it.dataOrNull }.first()
                if (his != null) {
                    history.value = his
                } else {
                    shouldKeepOnScreen = false
                }
            } else {
                shouldKeepOnScreen = false
            }
        }
    }

    fun completeTutorial() {
        val oldDoneTutorial = runBlocking { loadSettingsUseCase.settings.first() }.doneTutorial
        _uiEvents.tryEmit(ComicViewerAppUiEvent.CompleteTutorial(!oldDoneTutorial))
        viewModelScope.launch {
            loadSettingsUseCase.edit {
                it.copy(doneTutorial = true)
            }
        }
    }

    fun restoreComplete() {
        history.value = null
        shouldKeepOnScreen = false
    }
}
