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
import androidx.core.uri.Uri
import androidx.core.uri.UriUtils
import com.sorrowblue.comicviewer.feature.bookshelf.edit.InternalStorageEditScreenForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_select_folder
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_select_folder
import io.github.vinceglb.filekit.compose.PickerResultLauncher
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import logcat.logcat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notNull

expect fun localUriToDisplayPath(uri: Uri): String

@Composable
internal fun FolderSelectField(
    state: FolderSelectFieldState,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Controller(state.control) { field ->
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = field.value?.let { localUriToDisplayPath(it) }.orEmpty(),
            onValueChange = {},
            modifier = modifier
                .testTag("FolderSelect")
                .onFocusChanged {
                    if (it.isFocused) {
                        state.pickerResultLauncher.launch()
                        focusManager.clearFocus()
                    }
                },
            label = { Text(text = field.name) },
            isError = field.hasError,
            enabled = enabled && field.isEnabled,
            supportingText = {
                field.errors.firstOrNull()?.let { Text(text = it) }
            },
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
        )
    }
}

internal interface FolderSelectFieldState {
    val control: FieldControl<Uri?>
    val pickerResultLauncher: PickerResultLauncher
}

@Composable
internal fun FormScope<InternalStorageEditScreenForm>.rememberFolderSelectFieldState(
    onOpenDocumentTreeCancel: () -> Unit,
    control: FieldControl<Uri?> = rememberFolderSelectFieldControl(),
    takePersistableUriPermission: TakePersistableUriPermission = koinInject(),
    pickerResultLauncher: PickerResultLauncher = rememberDirectoryPickerLauncher { uri ->
        logcat { "PickerResultLauncher onResult uri=$uri" }
        uri?.path?.let(UriUtils::parse)?.let {
            takePersistableUriPermission(it)
            control.setValue(it)
        } ?: run {
            onOpenDocumentTreeCancel()
        }
    },
): FolderSelectFieldState {
    return remember {
        FolderSelectFieldStateImpl(control, pickerResultLauncher)
    }
}

private class FolderSelectFieldStateImpl(
    override val control: FieldControl<Uri?>,
    override val pickerResultLauncher: PickerResultLauncher,
) : FolderSelectFieldState

@Composable
private fun FormScope<InternalStorageEditScreenForm>.rememberFolderSelectFieldControl(): FieldControl<Uri?> {
    val message = stringResource(Res.string.bookshelf_edit_error_select_folder)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.bookshelf_edit_label_select_folder),
        select = { path },
        update = { copy(path = it) }
    ) {
        notNull { message }
    }
}

internal expect class TakePersistableUriPermission {

    operator fun invoke(uri: Uri)
}
