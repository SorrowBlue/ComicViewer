package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Folder
import androidx.compose.material.icons.twotone.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DeviceStorageInfoEditor(
    modifier: Modifier = Modifier,
    uiState: BookshelfEditorUiState.DeviceStorage = BookshelfEditorUiState.DeviceStorage(),
    onDisplayNameChange: (String) -> Unit = {},
    onSelectFolderClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
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
            value = uiState.dir,
            onValueChange = {},
            trailingIcon = {
                IconButton(onClick = onSelectFolderClick) {
                    Icon(Icons.TwoTone.Folder, null)
                }
            },
            enabled = false
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        val keyboardController = LocalSoftwareKeyboardController.current
        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                keyboardController?.hide()
                onSaveClick()
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                imageVector = Icons.TwoTone.Save,
                contentDescription = null
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Save")
        }
    }
}

@Preview
@Composable
fun PreivewDeviceStorageInfoEditor() {
    AppMaterialTheme {
        Surface {
            DeviceStorageInfoEditor()
        }
    }
}
