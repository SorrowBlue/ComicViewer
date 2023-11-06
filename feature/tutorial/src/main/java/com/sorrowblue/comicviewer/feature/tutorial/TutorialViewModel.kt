package com.sorrowblue.comicviewer.feature.tutorial

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

private const val DocumentModule = "document"

@HiltViewModel
internal class TutorialViewModel @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
    private val viewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
) : ViewModel(), DefaultLifecycleObserver, SplitInstallStateUpdatedListener {

    private val _uiState = MutableStateFlow(TutorialScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val uiState = _uiState.value
        if (splitInstallManager.installedModules.contains(DocumentModule)) {
            logcat { "Feature module($DocumentModule) is already installed." }
            _uiState.value =
                uiState.copy(documentSheetUiState = DocumentSheetUiState.INSTALLED)
        } else {
            logcat { "Feature module($DocumentModule) is not installed." }
            _uiState.value = uiState.copy(documentSheetUiState = DocumentSheetUiState.NONE)
        }
        viewerOperationSettingsUseCase.settings.onEach {
            _uiState.value = uiState.copy(
                directionSheetUiState = uiState.directionSheetUiState.copy(
                    direction = it.bindingDirection
                )
            )
        }.launchIn(viewModelScope)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        splitInstallManager.registerListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        splitInstallManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        if (state.moduleNames.contains(DocumentModule)) {
            val uiState = when (state.status) {
                SplitInstallSessionStatus.CANCELED -> DocumentSheetUiState.CANCELED
                SplitInstallSessionStatus.CANCELING -> DocumentSheetUiState.CANCELING
                SplitInstallSessionStatus.DOWNLOADED -> DocumentSheetUiState.DOWNLOADED
                SplitInstallSessionStatus.DOWNLOADING -> DocumentSheetUiState.DOWNLOADING(
                    (state.bytesDownloaded.toDouble() / state.totalBytesToDownload).toFloat()
                )

                SplitInstallSessionStatus.FAILED -> DocumentSheetUiState.FAILED(state.err)
                SplitInstallSessionStatus.INSTALLED -> DocumentSheetUiState.INSTALLED
                SplitInstallSessionStatus.INSTALLING -> DocumentSheetUiState.INSTALLING
                SplitInstallSessionStatus.PENDING -> DocumentSheetUiState.Pending
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> DocumentSheetUiState.RequestsUserConfirmation
                SplitInstallSessionStatus.UNKNOWN -> DocumentSheetUiState.NONE
                else -> DocumentSheetUiState.NONE
            }
            _uiState.value = _uiState.value.copy(documentSheetUiState = uiState)
        }
    }

    fun onDocumentDownloadClick() {
        viewModelScope.launch {
            splitInstallManager.requestInstall(listOf(DocumentModule))
        }
    }

    fun updateReadingDirection(rtl: BindingDirection) {
        viewModelScope.launch {
            viewerOperationSettingsUseCase.edit { it.copy(bindingDirection = rtl) }
        }
    }
}
