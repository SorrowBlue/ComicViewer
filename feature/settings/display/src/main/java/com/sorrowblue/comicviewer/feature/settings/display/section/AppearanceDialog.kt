package com.sorrowblue.comicviewer.feature.settings.display.section

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsViewModel
import com.sorrowblue.comicviewer.feature.settings.display.R
import com.sorrowblue.comicviewer.feature.settings.display.label
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavGraph
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface AppearanceDialogState {
    val darkMode: DarkMode
    fun onDarkModeChange(darkMode: DarkMode)
}

@Composable
internal fun rememberAppearanceDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: DisplaySettingsViewModel = hiltViewModel(),
): AppearanceDialogState = remember {
    AppearanceDialogStateImpl(
        scope = scope,
        displaySettingsUseCase = viewModel.displaySettingsUseCase
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

    override fun onDarkModeChange(darkMode: DarkMode) {
        scope.launch {
            displaySettingsUseCase.edit {
                it.copy(darkMode = darkMode)
            }
            when (darkMode) {
                DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }.let(AppCompatDelegate::setDefaultNightMode)
        }
    }
}

@Destination<DisplaySettingsNavGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun AppearanceDialog(destinationsNavigator: DestinationsNavigator) {
    val state: AppearanceDialogState = rememberAppearanceDialogState()
    AppearanceDialog(
        onDismissRequest = { destinationsNavigator.popBackStack() },
        currentDarkMode = state.darkMode,
        onDarkModeChange = {
            state.onDarkModeChange(it)
            destinationsNavigator.popBackStack()
        }
    )
}

@Composable
private fun AppearanceDialog(
    onDismissRequest: () -> Unit,
    currentDarkMode: DarkMode?,
    onDarkModeChange: (DarkMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.settings_display_label_appearance)) }
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
                    androidx.compose.material3.RadioButton(
                        selected = darkMode == currentDarkMode,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(text = stringResource(id = darkMode.label))
                }
            }
        }
    }
}
