package com.sorrowblue.comicviewer.feature.settings.display.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.display.R
import com.sorrowblue.comicviewer.feature.settings.display.label
import com.sorrowblue.comicviewer.framework.ui.DialogController
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.RadioButton
import com.sorrowblue.comicviewer.framework.ui.material3.Text

@Composable
internal fun AppearanceDialog(
    onDismissRequest: () -> Unit,
    currentDarkMode: DarkMode?,
    onDarkModeChange: (DarkMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(id = R.string.settings_display_label_appearance) }
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
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(id = darkMode.label)
                }
            }
        }
    }
}

class AppearanceDialogController : DialogController<DarkMode>(DarkMode.DEVICE)

@Composable
fun rememberAppearanceDialogController() = rememberSaveable(
    saver = Saver(save = {
        if (it.isShow) it.value else null
    }, restore = {
        AppearanceDialogController().apply {
            show(it)
        }
    })
) { AppearanceDialogController() }
