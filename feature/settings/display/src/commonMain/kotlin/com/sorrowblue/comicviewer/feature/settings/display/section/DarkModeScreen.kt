package com.sorrowblue.comicviewer.feature.settings.display.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.settings.display.label
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import com.sorrowblue.comicviewer.framework.ui.layout.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.display.generated.resources.Res
import comicviewer.feature.settings.display.generated.resources.settings_display_label_appearance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

interface AppearanceDialogState {
    val darkMode: DarkMode
    fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit)
}

@Composable
internal fun rememberAppearanceDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    displaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
): AppearanceDialogState = remember {
    AppearanceDialogStateImpl(
        scope = scope,
        displaySettingsUseCase = displaySettingsUseCase
    )
}

private class AppearanceDialogStateImpl(
    private val scope: CoroutineScope,
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
) : AppearanceDialogState {

    override var darkMode by mutableStateOf(DarkMode.DEVICE)
        private set

    init {
        displaySettingsUseCase.settings.onEach {
            darkMode = it.darkMode
        }.launchIn(scope)
    }

    override fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit) {
        scope.launch {
            displaySettingsUseCase.edit {
                it.copy(darkMode = darkMode)
            }
            updateDarkMode(darkMode)
            done()
        }
    }
}

internal expect fun updateDarkMode(darkMode: DarkMode)

@Serializable
data object DisplaySettingsDarkMode

@Destination<DisplaySettingsDarkMode>(style = DestinationStyle.Dialog::class)
@Composable
internal fun DarkModeScreen(navController: NavController) {
    val state: AppearanceDialogState = rememberAppearanceDialogState()
    DarkModeScreen(
        onDismissRequest = { navController.popBackStack() },
        currentDarkMode = state.darkMode,
        onDarkModeChange = {
            state.onDarkModeChange(it) {
                navController.popBackStack()
            }
        }
    )
}

@Composable
private fun DarkModeScreen(
    onDismissRequest: () -> Unit,
    currentDarkMode: DarkMode?,
    onDarkModeChange: (DarkMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_display_label_appearance)) }
    ) {
        Column(Modifier.padding(it.copy(start = 0.dp, end = 0.dp))) {
            DarkMode.entries.forEach { darkMode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDarkModeChange(darkMode) }
                        .padding(it.copy(top = 0.dp, bottom = 0.dp))
                        .padding(vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = darkMode == currentDarkMode,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(text = stringResource(darkMode.label))
                }
            }
        }
    }
}
