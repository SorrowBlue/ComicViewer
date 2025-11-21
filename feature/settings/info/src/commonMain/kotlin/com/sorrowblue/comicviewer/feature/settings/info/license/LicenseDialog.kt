package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.entity.Library
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_label_close
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LicenseDialog(library: Library, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.settings_info_label_close))
            }
        },
        title = {
            Text(
                library.licenses
                    .firstOrNull()
                    ?.name
                    .orEmpty(),
            )
        },
        text = {
            val scrollState = rememberScrollState()
            Text(
                text = library.licenses
                    .firstOrNull()
                    ?.licenseContent
                    .orEmpty(),
                modifier = Modifier.verticalScroll(scrollState),
            )
        },
    )
}
