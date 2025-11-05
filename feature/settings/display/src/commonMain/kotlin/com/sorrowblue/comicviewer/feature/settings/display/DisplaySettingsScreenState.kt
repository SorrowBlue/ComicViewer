package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
internal interface DisplaySettingsScreenState {

    val uiState: SettingsDisplayScreenUiState

    fun onRestoreOnLaunchChange(value: Boolean)
}

@Composable
context(context: DisplaySettingsScreenContext)
internal fun rememberDisplaySettingsScreenState(): DisplaySettingsScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        DisplaySettingsScreenStateImpl(
            coroutineScope = coroutineScope,
            displaySettingsUseCase = context.displaySettingsUseCase
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
}

private class DisplaySettingsScreenStateImpl(
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
    var coroutineScope: CoroutineScope,
) : DisplaySettingsScreenState {

    override var uiState by mutableStateOf(SettingsDisplayScreenUiState())
        private set

    init {
        displaySettingsUseCase.settings.onEach {
            uiState = uiState.copy(darkMode = it.darkMode, restoreOnLaunch = it.restoreOnLaunch)
        }.launchIn(coroutineScope)
    }

    override fun onRestoreOnLaunchChange(value: Boolean) {
        coroutineScope.launch {
            displaySettingsUseCase.edit {
                it.copy(restoreOnLaunch = value)
            }
        }
    }
}
