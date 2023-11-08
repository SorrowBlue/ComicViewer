package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.autofill.connectNode
import com.sorrowblue.comicviewer.framework.ui.autofill.defaultFocusChangeAutoFill
import com.sorrowblue.comicviewer.framework.ui.autofill.rememberAutoFillRequestHandler
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.OutlinedTextField2
import com.sorrowblue.comicviewer.framework.ui.material3.ValidateOutlinedTextField
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun DisplayNameField(
    input: Input,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ValidateOutlinedTextField(
        input = input,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_label_display_name)) },
        errorText = {
            Text(text = stringResource(id = R.string.bookshelf_edit_error_display_name))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@Composable
internal fun FolderSelectField(input: Input, onClick: () -> Unit) {
    val focusManager = LocalFocusManager.current
    ValidateOutlinedTextField(
        input = input,
        onValueChange = {},
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_label_select_folder)) },
        placeholder = { Text(text = stringResource(id = R.string.bookshelf_edit_label_select_folder)) },
        errorText = { Text(text = stringResource(id = R.string.bookshelf_edit_error_select_folder)) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = ComicIcons.Folder,
                    contentDescription = stringResource(id = R.string.bookshelf_edit_label_select_folder)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    onClick()
                    focusManager.clearFocus()
                }
            },
    )
}

@Composable
internal fun HostField(
    input: Input,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ValidateOutlinedTextField(
        input = input,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_host)) },
        errorText = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_host))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@Composable
internal fun PortField(
    input: Input,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ValidateOutlinedTextField(
        input = input,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_port)) },
        errorText = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_port))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@Composable
internal fun PathField(
    input: Input,
    auth: SmbEditScreenUiState.Auth,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ValidateOutlinedTextField(
        input = input,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_path)) },
        prefix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_prefix_path)) },
        suffix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_suffix_path)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = if (auth == SmbEditScreenUiState.Auth.Guest) {
                ImeAction.Done
            } else {
                ImeAction.Next
            }
        ),
        modifier = modifier
    )
}

@Composable
internal fun DomainField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField2(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_hint_domain)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun UsernameField(
    input: Input,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val usernameAutoFillHandler = rememberAutoFillRequestHandler(
        autofillTypes = remember { persistentListOf(AutofillType.Username) },
        onFill = onValueChange
    )
    ValidateOutlinedTextField(
        input = input,
        onValueChange = {
            onValueChange(it)
            if (it.isEmpty()) usernameAutoFillHandler.requestVerifyManual()
        },
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_hint_username)) },
        errorText = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_username))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
            .connectNode(handler = usernameAutoFillHandler)
            .defaultFocusChangeAutoFill(handler = usernameAutoFillHandler)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PasswordField(
    input: Input,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordAutoFillHandler = rememberAutoFillRequestHandler(
        autofillTypes = remember { persistentListOf(AutofillType.Password) },
        onFill = onValueChange
    )
    ValidateOutlinedTextField(
        input = input,
        onValueChange = {
            onValueChange(it)
            if (it.isEmpty()) passwordAutoFillHandler.requestVerifyManual()
        },
        errorText = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_password)) },
        label = { Text(text = stringResource(id = R.string.bookshelf_edit_hint_password)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        modifier = modifier
            .connectNode(handler = passwordAutoFillHandler)
            .defaultFocusChangeAutoFill(handler = passwordAutoFillHandler)
    )
}
