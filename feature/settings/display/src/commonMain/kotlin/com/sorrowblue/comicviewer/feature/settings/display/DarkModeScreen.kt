package com.sorrowblue.comicviewer.feature.settings.display

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.ui.layout.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.display.generated.resources.Res
import comicviewer.feature.settings.display.generated.resources.settings_display_label_appearance
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

internal data class DarkModeScreenUiState(
    val list: ImmutableList<DarkMode> = DarkMode.entries.toImmutableList(),
    val darkMode: DarkMode = DarkMode.DEVICE,
)

@Composable
internal fun DarkModeScreen(
    uiState: DarkModeScreenUiState,
    onDismissRequest: () -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_display_label_appearance)) }
    ) {
        Column(Modifier.padding(it.copy(start = 0.dp, end = 0.dp))) {
            uiState.list.forEach { darkMode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDarkModeChange(darkMode) }
                        .padding(it.copy(top = 0.dp, bottom = 0.dp))
                        .padding(vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = darkMode == uiState.darkMode,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(text = stringResource(darkMode.label))
                }
            }
        }
    }
}
