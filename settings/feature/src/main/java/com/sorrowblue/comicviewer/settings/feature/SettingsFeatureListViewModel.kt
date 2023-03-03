package com.sorrowblue.comicviewer.settings.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.requestCancelInstall
import com.google.android.play.core.ktx.requestDeferredUninstall
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.splitinstall.SplitInstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsFeatureListViewModel @Inject constructor(
    val splitInstallManager: SplitInstallManager
) : ViewModel() {

    val stateList: MutableStateFlow<List<FeatureItem>>
    init {
        val modules = splitInstallManager.installedModules
        stateList = MutableStateFlow(Feature.values().map {
            FeatureItem(
                it,
                if (modules.contains(it.moduleName)) InstallStatus.Installed else InstallStatus.NotInstall
            )
        })
    }


    fun startInstall(moduleName: String) {
        viewModelScope.launch {
            val sessionId = splitInstallManager.requestInstall(listOf(moduleName))
            stateList.value = stateList.value.map {
                if (it.feature.moduleName == moduleName) it.copy(sessionId = sessionId) else it
            }
        }
    }

    fun cancel(sessionId: Int) {
        viewModelScope.launch {
            splitInstallManager.requestCancelInstall(sessionId)
        }
    }

    fun uninstall(moduleName: String) {
        viewModelScope.launch {
            splitInstallManager.requestDeferredUninstall(listOf(moduleName))
        }
    }

}
