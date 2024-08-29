package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.InternalStorageEditScreenForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import logcat.logcat
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notNull

@Composable
internal fun FolderSelectField(
    state: FolderSelectFieldState,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Controller(state.control) { field ->
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = field.value?.lastPathSegment?.split(":")?.lastOrNull().orEmpty(),
            onValueChange = {},
            modifier = modifier
                .testTag("FolderSelect")
                .onFocusChanged {
                    if (it.isFocused) {
                        state.activityResultLauncher.launch(field.value)
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
                IconButton(onClick = { state.activityResultLauncher.launch(field.value) }) {
                    Icon(
                        imageVector = ComicIcons.Folder,
                        contentDescription = stringResource(id = R.string.bookshelf_edit_label_select_folder)
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
    val activityResultLauncher: ManagedActivityResultLauncher<Uri?, Uri?>
}

@Composable
internal fun FormScope<InternalStorageEditScreenForm>.rememberFolderSelectFieldState(
    onOpenDocumentTreeCancel: () -> Unit,
    context: Context = LocalContext.current,
    control: FieldControl<Uri?> = rememberFolderSelectFieldControl(),
    activityResultLauncher: ManagedActivityResultLauncher<Uri?, Uri?> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            logcat { "OpenDocumentTree() onResult uri=$uri" }
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                control.setValue(it)
            } ?: run {
                onOpenDocumentTreeCancel()
            }
        }
    ),
): FolderSelectFieldState {
    return remember {
        FolderSelectFieldStateImpl(control, activityResultLauncher)
    }
}

private class FolderSelectFieldStateImpl(
    override val control: FieldControl<Uri?>,
    override val activityResultLauncher: ManagedActivityResultLauncher<Uri?, Uri?>,
) : FolderSelectFieldState

@Composable
private fun FormScope<InternalStorageEditScreenForm>.rememberFolderSelectFieldControl(): FieldControl<Uri?> {
    val message = stringResource(R.string.bookshelf_edit_error_select_folder)
    return rememberFieldRuleControl(
        name = stringResource(R.string.bookshelf_edit_label_select_folder),
        select = { path },
        update = { copy(path = it) }
    ) {
        notNull { message }
    }
}
