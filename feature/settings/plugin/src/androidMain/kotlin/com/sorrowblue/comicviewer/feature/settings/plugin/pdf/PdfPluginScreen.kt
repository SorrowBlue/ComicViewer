package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.FilePdf
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.immatureRectangleProgressBorder
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialogContent
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.plugin.generated.resources.Res
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_title_pdf
import org.jetbrains.compose.resources.stringResource

data class PdfPluginScreenUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)

@Composable
internal fun PdfPluginScreen(uiState: PdfPluginScreenUiState, onOpenFolderClick: () -> Unit) {
    AlertDialog(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(Res.string.settings_plugin_title_pdf))
                CloseIconButton(onClick = {})
            }
        },
        onDismissRequest = {},
    ) {
        AlertDialogContent(
            icon = {
                Image(ComicIcons.FilePdf, null, Modifier.size(64.dp))
            },
        ) {
            OutlinedTextField(
                label = {
                    Text("インストールフォルダを選択")
                },
                value = uiState.folderPath,
                onValueChange = {},
                readOnly = true,
                enabled = !uiState.checking,
                supportingText = {
                    if (uiState.error.isNotEmpty()) {
                        Text(uiState.error, color = ComicTheme.colorScheme.error)
                    } else if (uiState.info.isNotEmpty()) {
                        Text(uiState.info, color = ComicTheme.colorScheme.tertiary)
                    }
                },
                trailingIcon = {
                    if (uiState.checking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                    } else {
                        IconButton(
                            onClick = onOpenFolderClick,
                        ) {
                            Icon(ComicIcons.FolderOpen, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .testTag("FolderSelect")
                    .immatureRectangleProgressBorder(
                        color = ComicTheme.colorScheme.secondary,
                        enable = uiState.checking,
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun PdfPluginScreenPreview() {
    PreviewTheme {
        PdfPluginScreen(
            uiState = remember { PdfPluginScreenUiState() },
            onOpenFolderClick = {},
        )
    }
}
