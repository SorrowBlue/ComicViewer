package com.sorrowblue.comicviewer.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.AddOn
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class ComicViewerAppViewModel @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel(), DefaultLifecycleObserver {

    private val _uiEvents =
        MutableSharedFlow<ComicViewerAppUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvents = _uiEvents.asSharedFlow()

    var shouldKeepOnScreen = true

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
            if (!loadSettingsUseCase.settings.first().doneTutorial) {
                _uiEvents.emit(ComicViewerAppUiEvent.StartTutorial)
                shouldKeepOnScreen = false
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
}
