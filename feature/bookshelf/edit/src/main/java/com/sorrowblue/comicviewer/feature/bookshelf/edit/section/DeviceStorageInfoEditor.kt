package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Composable
fun DeviceStorageInfoEditor(
    modifier: Modifier = Modifier,
    uiState: BookshelfEditorUiState.DeviceStorage = BookshelfEditorUiState.DeviceStorage(),
    isRunning: Boolean = false,
    onDisplayNameChange: (String) -> Unit = {},
    onSelectFolderClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    Box {
        Column(modifier) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.displayName,
                label = { Text(text = "表示名") },
                isError = uiState.isDisplayNameError,
                onValueChange = { onDisplayNameChange(it) },
                supportingText = {
                    if (uiState.isDisplayNameError) {
                        Text(text = "ERROR")
                    }
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "フォルダを選択")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTrailingIconColor = ComicTheme.colorScheme.primary,
                    disabledContainerColor = Color.Transparent,
                    disabledBorderColor = ComicTheme.colorScheme.outline,
                    disabledLabelColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledPrefixColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledSuffixColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = ComicTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor = ComicTheme.colorScheme.onSurface
                ),
                value = uiState.dir,
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = onSelectFolderClick) {
                        Icon(ComicIcons.Folder, null)
                    }
                },
                enabled = false
            )
            Spacer(modifier = Modifier.weight(1f))
            val keyboardController = LocalSoftwareKeyboardController.current
            FilledTonalButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    keyboardController?.hide()
                    onSaveClick()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                enabled = uiState.validate
            ) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = ComicIcons.Save,
                    contentDescription = null
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Save")
            }
        }
        if (isRunning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComicTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun PreviewDeviceStorageInfoEditor() {
    PreviewTheme {
        Surface {
            DeviceStorageInfoEditor(isRunning = true)
        }
    }
}
