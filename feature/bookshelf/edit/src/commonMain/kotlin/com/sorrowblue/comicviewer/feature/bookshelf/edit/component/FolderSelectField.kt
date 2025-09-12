package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.InternalStorageEditScreenForm
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_select_folder
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_select_folder
import io.github.vinceglb.filekit.compose.PickerResultLauncher
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.core.PlatformDirectory
import logcat.logcat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.annotation.Single
import soil.form.FieldValidator
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.hasError
import soil.form.compose.rememberField
import soil.form.compose.watch
import soil.form.rule.notNull

expect fun localUriToDisplayPath(path: String): String
expect val PlatformDirectory.pathString: String

@Composable
internal fun FolderSelectField(
    state: FolderSelectFieldState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = state.formField.value?.let { localUriToDisplayPath(it) }.orEmpty(),
        onValueChange = {},
        label = { Text(text = state.formField.name) },
        isError = state.formField.hasError,
        enabled = state.formField.isEnabled,
        supportingText = state.formField.supportingText(),
        trailingIcon = {
            IconButton(onClick = { state.pickerResultLauncher.launch() }) {
                Icon(
                    imageVector = ComicIcons.Folder,
                    contentDescription = stringResource(Res.string.bookshelf_edit_label_select_folder)
                )
            }
        },
        readOnly = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
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
    form: Form<InternalStorageEditScreenForm>,
    onOpenDocumentTreeCancel: () -> Unit,
    formField: FormField<String?> = form.rememberFolderSelectField(),
    takePersistableUriPermission: TakePersistableUriPermission = koinInject(),
    pickerResultLauncher: PickerResultLauncher = rememberDirectoryPickerLauncher { platformDirectory ->
        logcat(
            "FolderSelectFieldState"
        ) { "PickerResultLauncher onResult uri=$platformDirectory, pathString=${platformDirectory?.pathString}" }
        platformDirectory?.let {
            takePersistableUriPermission(it)
            formField.onValueChange(it.pathString)
        } ?: run {
            onOpenDocumentTreeCancel()
        }
    },
): FolderSelectFieldState {
    return remember {
        FolderSelectFieldStateImpl(formField, pickerResultLauncher)
    }
}

private class FolderSelectFieldStateImpl(
    override val formField: FormField<String?>,
    override val pickerResultLauncher: PickerResultLauncher,
) : FolderSelectFieldState

@Composable
private fun Form<InternalStorageEditScreenForm>.rememberFolderSelectField(): FormField<String?> {
    val errorMessage = stringResource(Res.string.bookshelf_edit_error_select_folder)
    return rememberField(
        name = stringResource(Res.string.bookshelf_edit_label_select_folder),
        selector = { it.path },
        updater = { this.copy(path = it) },
        validator = FieldValidator { notNull { errorMessage } },
        enabled = watch { !value.isRunning }
    )
}

@Single
internal expect class TakePersistableUriPermission(context: PlatformContext) {

    operator fun invoke(platformDirectory: PlatformDirectory)
}
