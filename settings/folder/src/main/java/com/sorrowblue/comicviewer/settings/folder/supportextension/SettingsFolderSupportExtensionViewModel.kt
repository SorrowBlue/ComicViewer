package com.sorrowblue.comicviewer.settings.folder.supportextension

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.sessionId
import com.google.android.play.core.ktx.status
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsFolderSupportExtensionViewModel @Inject constructor(
    application: Application,
    private val settingsUseCase: ManageFolderSettingsUseCase,
    private val splitInstallManager: SplitInstallManager
) : AndroidViewModel(application) {

    val isSupportDocument = MutableStateFlow(splitInstallManager.installedModules.contains("document"))

    val supportExtension = settingsUseCase.settings.map { it.supportExtension }

    fun install() {
        viewModelScope.launch {
            val sessionId = splitInstallManager.requestInstall(listOf("document"))
            splitInstallManager.registerListener {
                if (it.sessionId == sessionId) {
                    when (it.status) {
                        SplitInstallSessionStatus.CANCELED -> {
                            TODO()
                        }

                        SplitInstallSessionStatus.CANCELING -> {}

                        SplitInstallSessionStatus.DOWNLOADED -> {
                        }

                        SplitInstallSessionStatus.DOWNLOADING -> {
                        }

                        SplitInstallSessionStatus.FAILED -> {
                            TODO()
                        }

                        SplitInstallSessionStatus.INSTALLED -> {
                            isSupportDocument.value = splitInstallManager.installedModules.contains("document")
                        }

                        SplitInstallSessionStatus.INSTALLING -> {
                            TODO()
                        }

                        SplitInstallSessionStatus.PENDING,
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
                        SplitInstallSessionStatus.UNKNOWN -> {
                        }
                    }
                }
            }
        }
    }

    fun removeExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.remove(extension)
                it.copy(supportExtension = list.sortedBy(SupportExtension::extension).toSet())
            }
        }
    }

    fun addExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.add(extension)
                it.copy(supportExtension = list.sortedBy(SupportExtension::extension).toSet())
            }
        }
    }
}
