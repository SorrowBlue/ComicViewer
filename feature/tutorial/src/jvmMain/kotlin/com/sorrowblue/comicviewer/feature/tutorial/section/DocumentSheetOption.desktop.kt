package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.immatureRectangleProgressBorder
import comicviewer.feature.tutorial.generated.resources.Res
import comicviewer.feature.tutorial.generated.resources.tutorial_text_document_btn_download
import org.jetbrains.compose.resources.stringResource

data class DocumentSheetUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)

@Composable
internal actual fun DocumentSheetOption(modifier: Modifier) {
    val state = with(LocalPlatformContext.current.rememberDocumentSheetContext()) {
        rememberDocumentSheetState()
    }
    DocumentSheetOption(
        uiState = state.uiState,
        onDocumentDownloadClick = state::onDocumentDownloadClick,
        onOpenFolderClick = state::onOpenFolderClick,
    )
}

@Composable
private fun DocumentSheetOption(
    uiState: DocumentSheetUiState,
    onDocumentDownloadClick: () -> Unit,
    onOpenFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onDocumentDownloadClick) {
            Row {
                Icon(ComicIcons.InstallMobile, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(Res.string.tutorial_text_document_btn_download))
            }
        }
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
