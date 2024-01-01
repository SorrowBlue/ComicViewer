package com.sorrowblue.comicviewer.feature.settings.folder

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
internal interface SupportExtensionScreenState {
    val uiState: SupportExtensionScreenUiState
    fun toggleExtension(supportExtension: SupportExtension)
}

@Composable
internal fun rememberSupportExtensionScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SupportExtensionViewModel = hiltViewModel(),
): SupportExtensionScreenState = remember {
    val splitInstallManager = SplitInstallManagerFactory.create(context)
    SupportExtensionScreenStateImpl(
        scope = scope,
        splitInstallManager = splitInstallManager,
        viewModel = viewModel
    )
}

private class SupportExtensionScreenStateImpl(
    scope: CoroutineScope,
    splitInstallManager: SplitInstallManager,
    private val viewModel: SupportExtensionViewModel,
) : SupportExtensionScreenState {

    override var uiState by mutableStateOf(
        SupportExtensionScreenUiState(
            isDocumentInstalled = splitInstallManager.installedModules.contains("document")
        )
    )
        private set

    init {
        viewModel.settingsFlow.onEach {
            uiState = uiState.copy(supportExtension = it)
        }.launchIn(scope)
    }

    override fun toggleExtension(supportExtension: SupportExtension) {
        viewModel.toggleExtension(supportExtension)
    }
}
