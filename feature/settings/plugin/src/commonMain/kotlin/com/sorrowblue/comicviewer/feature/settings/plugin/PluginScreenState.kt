package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface PluginScreenState {
    val uiState: PluginScreenUiState
}

@Composable
context(context: PluginScreenContext)
internal fun rememberPluginScreenState(): PluginScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        PluginScreenStateImpl(
            coroutineScope = coroutineScope,
            managePdfPluginSettingsUseCase = context.managePdfPluginSettingsUseCase,
        )
    }
}

private class PluginScreenStateImpl(
    coroutineScope: CoroutineScope,
    val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase,
) : PluginScreenState {
    override var uiState by mutableStateOf(PluginScreenUiState())

    init {
        managePdfPluginSettingsUseCase.settings.onEach {
            uiState = uiState.copy(isPdfPluginEnable = it.isEnabled)
        }.launchIn(coroutineScope)
    }
}
