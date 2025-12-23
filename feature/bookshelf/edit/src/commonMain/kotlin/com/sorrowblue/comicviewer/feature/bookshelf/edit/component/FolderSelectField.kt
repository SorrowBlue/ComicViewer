package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.InternalStorageEditForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_select_folder
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_select_folder
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.rule.notNull

expect fun localUriToDisplayPath(path: String): String

@Composable
internal fun FolderSelectField(state: FolderSelectFieldState, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = state.formField.value
            ?.let { localUriToDisplayPath(it) }
            .orEmpty(),
        onValueChange = {},
        label = { Text(text = stringResource(Res.string.bookshelf_edit_label_select_folder)) },
        isError = state.formField.hasError,
        enabled = state.formField.isEnabled,
        supportingText = state.formField.supportingText(),
        trailingIcon = {
            IconButton(onClick = { state.pickerResultLauncher.launch() }) {
                Icon(
                    imageVector = ComicIcons.Folder,
                    contentDescription = stringResource(
                        Res.string.bookshelf_edit_label_select_folder,
                    ),
                )
            }
        },
        readOnly = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
        modifier = modifier
            .testTag("FolderSelect")
            .onFocusChanged {
                if (it.isFocused) {
                    state.pickerResultLauncher.launch()
                    focusManager.clearFocus()
                }
            },
    )
}

internal interface FolderSelectFieldState {
    val formField: FormField<String?>
    val pickerResultLauncher: PickerResultLauncher
}

@Composable
internal fun rememberFolderSelectFieldState(
    form: Form<InternalStorageEditForm>,
    onOpenDocumentTreeCancel: () -> Unit,
    formField: FormField<String?> = form.rememberFolderSelectField(),
    scope: CoroutineScope = rememberCoroutineScope(),
    pickerResultLauncher: PickerResultLauncher =
        rememberDirectoryPickerLauncher { platformDirectory ->
            logcat(
                "FolderSelectFieldState",
            ) {
                "PickerResultLauncher onResult uri=$platformDirectory, pathString=${platformDirectory?.path}"
            }
            platformDirectory?.let {
                scope.launch {
                    // Take a persistable URI permission for the selected directory
                    platformDirectory.bookmarkData()
                    formField.onValueChange(it.path)
                }
            } ?: run {
                onOpenDocumentTreeCancel()
            }
        },
): FolderSelectFieldState = remember {
    FolderSelectFieldStateImpl(formField, pickerResultLauncher)
}

private class FolderSelectFieldStateImpl(
    override val formField: FormField<String?>,
    override val pickerResultLauncher: PickerResultLauncher,
) : FolderSelectFieldState

@Composable
private fun Form<InternalStorageEditForm>.rememberFolderSelectField(): FormField<String?> {
    val errorMessage = stringResource(Res.string.bookshelf_edit_error_select_folder)
    return rememberField(
        name = FolderSelectFieldName,
        selector = { it.path },
        updater = { this.copy(path = it) },
        validator = FieldValidator { notNull { errorMessage } },
    )
}

internal const val FolderSelectFieldName = "FolderSelectField"
