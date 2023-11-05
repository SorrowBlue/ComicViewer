package com.sorrowblue.comicviewer.feature.library

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.requestSessionStates
import com.google.android.play.core.ktx.status
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.section.RequestInstallDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
) : ViewModel(),
    DefaultLifecycleObserver,
    SplitInstallStateUpdatedListener {

    private val _uiState: MutableStateFlow<LibraryScreenUiState>

    init {
        val installedModules = splitInstallManager.installedModules
        val addOns = listOf(
            Feature.AddOn.GoogleDrive(AddOnItemState.Still),
            Feature.AddOn.OneDrive(AddOnItemState.Still),
            Feature.AddOn.Dropbox(AddOnItemState.Still),
            Feature.AddOn.Box(AddOnItemState.Still)
        ).map {
            if (installedModules.contains(it.addOn.moduleName)) it.copy2(AddOnItemState.Installed) else it
        }
        _uiState =
            MutableStateFlow(LibraryScreenUiState(addOnList = addOns.toPersistentList()))
    }

    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LibraryScreenUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvent = _uiEvent.asSharedFlow()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        splitInstallManager.registerListener(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val installedModules = splitInstallManager.installedModules
        val list = _uiState.value.addOnList.map {
            it.copy2(
                if (installedModules.contains(it.addOn.moduleName)) AddOnItemState.Installed else AddOnItemState.Still
            )
        }
        _uiState.value = _uiState.value.copy(addOnList = list.toPersistentList())
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        splitInstallManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        val featureList = _uiState.value.addOnList.map {
            if (state.moduleNames.contains(it.addOn.moduleName)) {
                val addOnItemState = when (state.status) {
                    SplitInstallSessionStatus.CANCELED -> AddOnItemState.Still

                    SplitInstallSessionStatus.FAILED -> {
                        _uiEvent.tryEmit(
                            LibraryScreenUiEvent.Message2(
                                text = StringResource(
                                    R.string.library_message_addon_install_failed,
                                    state.errorCode
                                )
                            )
                        )
                        AddOnItemState.Failed
                    }

                    SplitInstallSessionStatus.CANCELING,
                    SplitInstallSessionStatus.DOWNLOADED,
                    SplitInstallSessionStatus.DOWNLOADING,
                    SplitInstallSessionStatus.INSTALLING,
                    SplitInstallSessionStatus.PENDING,
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
                    SplitInstallSessionStatus.UNKNOWN,
                    -> AddOnItemState.Installing

                    SplitInstallSessionStatus.INSTALLED -> {
                        _uiEvent.tryEmit(
                            LibraryScreenUiEvent.Message2(
                                text = StringResource(
                                    R.string.library_message_addon_installed,
                                    it.addOn.moduleName
                                ),
                                actionLabel = StringResource(R.string.library_label_restart),
                                duration = SnackbarDuration.Long
                            ) {
                                if (it == SnackbarResult.ActionPerformed) {
                                    _uiEvent.tryEmit(LibraryScreenUiEvent.Restart)
                                }
                            }
                        )
                        AddOnItemState.Restart
                    }

                    else -> AddOnItemState.Installing
                }
                it.copy2(state = addOnItemState)
            } else {
                it
            }
        }.toPersistentList()
        _uiState.value = _uiState.value.copy(addOnList = featureList)
    }

    fun startInstall() {
        val uiState = _uiState.value.requestInstallDialogUiState
        if (uiState is RequestInstallDialogUiState.Show) {
            val feature = uiState.feature
            _uiState.value =
                _uiState.value.copy(requestInstallDialogUiState = RequestInstallDialogUiState.Hide)
            viewModelScope.launch {
                if (splitInstallManager.isInstallAllowed()) {
                    val state = kotlin.runCatching {
                        splitInstallManager.requestInstall(listOf(feature.addOn.moduleName))
                    }.onFailure {
                        if (it is SplitInstallException) {
                            _uiEvent.tryEmit(
                                LibraryScreenUiEvent.Message2(
                                    text = StringResource(
                                        R.string.library_message_addon_install_failed,
                                        it.errorCode
                                    )
                                )
                            )
                            _uiState.value =
                                _uiState.value.copy(
                                    addOnList = _uiState.value.addOnList.map {
                                        if (it == feature) {
                                            it.copy2(state = AddOnItemState.Failed)
                                        } else {
                                            it
                                        }
                                    }.toPersistentList()
                                )
                        }
                    }.getOrNull()
                    if (state == 0) {
                        // 既にインストールされている場合
                        _uiState.value =
                            _uiState.value.copy(
                                addOnList = _uiState.value.addOnList.map {
                                    if (it == feature) {
                                        it.copy2(state = AddOnItemState.Installed)
                                    } else {
                                        it
                                    }
                                }.toPersistentList()
                            )
                        _uiEvent.emit(
                            LibraryScreenUiEvent.Message2(
                                text = StringResource(
                                    R.string.library_message_addon_already_installed
                                )
                            )
                        )
                    } else if (state != null) {
                        _uiEvent.emit(
                            LibraryScreenUiEvent.Message2(
                                text = StringResource(
                                    R.string.library_message_addon_installing,
                                    feature.addOn.moduleName
                                )
                            )
                        )
                    }
                } else {
                    _uiEvent.emit(
                        LibraryScreenUiEvent.Message2(
                            text = StringResource(R.string.library_message_other_addon_installing)
                        )
                    )
                }
            }
        }
    }

    fun onFeatureClick(feature: Feature) {
        when (feature) {
            is Feature.AddOn -> {
                when (feature.state) {
                    AddOnItemState.Still -> requestInstall(feature)
                    AddOnItemState.Installing -> Unit
                    AddOnItemState.Installed ->
                        _uiEvent.tryEmit(LibraryScreenUiEvent.ClickFeature(feature))

                    AddOnItemState.Restart ->
                        _uiEvent.tryEmit(
                            LibraryScreenUiEvent.Message2(
                                text = StringResource(R.string.library_message_restart_app),
                                actionLabel = StringResource(R.string.library_label_restart),
                            ) {
                                if (it == SnackbarResult.ActionPerformed) {
                                    _uiEvent.tryEmit(LibraryScreenUiEvent.Restart)
                                }
                            }
                        )

                    AddOnItemState.Failed -> requestInstall(feature)
                }
            }

            is Feature.Basic -> {
                _uiEvent.tryEmit(LibraryScreenUiEvent.ClickFeature(feature))
            }
        }
    }

    private fun requestInstall(feature: Feature.AddOn) {
        _uiState.value = _uiState.value.copy(
            requestInstallDialogUiState = RequestInstallDialogUiState.Show(feature)
        )
    }

    fun closeDialog() {
        _uiState.value =
            _uiState.value.copy(requestInstallDialogUiState = RequestInstallDialogUiState.Hide)
    }
}

private val allowInstallStatus = listOf(
    SplitInstallSessionStatus.INSTALLED,
    SplitInstallSessionStatus.FAILED,
    SplitInstallSessionStatus.CANCELED,
)

private suspend fun SplitInstallManager.isInstallAllowed(): Boolean {
    val states = requestSessionStates()
    return states.isEmpty() || states.all { it.status in allowInstallStatus }
}
