package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal interface DarkModeScreenState {
    val uiState: DarkModeScreenUiState

    fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit)
}

@Composable
context(context: DarkModeScreenContext)
internal fun rememberDarkModeScreenState(): DarkModeScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        DarkModeScreenStateImpl(
            displaySettingsUseCase = context.displaySettingsUseCase,
            coroutineScope = coroutineScope,
        )
    }
}

private class DarkModeScreenStateImpl(
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val coroutineScope: CoroutineScope,
) : DarkModeScreenState {
    override var uiState by mutableStateOf(DarkModeScreenUiState())

    init {
        displaySettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(darkMode = it.darkMode)
            }.launchIn(coroutineScope)
    }

    override fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit) {
        coroutineScope.launch {
            displaySettingsUseCase.edit {
                it.copy(darkMode = darkMode)
            }
            updateDarkMode(darkMode)
            done()
        }
    }
}

internal expect fun updateDarkMode(darkMode: DarkMode)
