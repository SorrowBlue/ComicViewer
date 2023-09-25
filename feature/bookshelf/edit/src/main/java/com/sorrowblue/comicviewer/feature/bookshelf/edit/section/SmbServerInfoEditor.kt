package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.autofill.connectNode
import com.sorrowblue.comicviewer.framework.ui.autofill.defaultFocusChangeAutoFill
import com.sorrowblue.comicviewer.framework.ui.autofill.rememberAutoFillRequestHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SmbServerInfoEditor(
    modifier: Modifier = Modifier,
    uiState: BookshelfEditorUiState.SmbServer = BookshelfEditorUiState.SmbServer(),
    onDisplayNameChange: (String) -> Unit = {},
    onHostChange: (String) -> Unit = {},
    onPortChange: (String) -> Unit = {},
    onPathChange: (String) -> Unit = {},
    onAuthMethodChange: (AuthMethod) -> Unit = {},
    onDomainChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(ComicTheme.dimension.margin)
            .imePadding()
    ) {
        OutlinedTextField(
            value = uiState.displayName,
            onValueChange = onDisplayNameChange,
            label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name)) },
            isError = uiState.isDisplayNameError,
            supportingText = {
                if (uiState.isDisplayNameError) {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = when (uiState.authMethod) {
                    AuthMethod.GUEST -> ImeAction.Done
                    AuthMethod.USERPASS -> ImeAction.Next
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = uiState.host,
            onValueChange = onHostChange,
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_host)) },
            isError = uiState.isHostError,
            supportingText = {
                if (uiState.isHostError) {
                    Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_host))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = uiState.port,
            onValueChange = onPortChange,
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_port)) },
            isError = uiState.isPortError,
            supportingText = {
                if (uiState.isPortError) {
                    Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_port))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = uiState.path,
            onValueChange = onPathChange,
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_path)) },
            prefix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_prefix_path)) },
            suffix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_suffix_path)) },
            supportingText = {},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(16.dp))
        MaterialButtons(
            size = AuthMethod.entries.size,
            label = {
                Text(
                    stringResource(
                        when (AuthMethod.entries[it]) {
                            AuthMethod.GUEST -> R.string.bookshelf_manage_label_guest
                            AuthMethod.USERPASS -> R.string.bookshelf_manage_label_username_password
                        }
                    )
                )
            },
            selectedIndex = uiState.authMethod.ordinal,
            onChange = {
                onAuthMethodChange(AuthMethod.entries[it])
            },
        )
        when (uiState.authMethod) {
            AuthMethod.GUEST -> Unit
            AuthMethod.USERPASS -> {
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = uiState.domain,
                    onValueChange = onDomainChange,
                    label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_domain)) },
                    singleLine = true,
                    supportingText = {},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                val usernameAutoFillHandler = rememberAutoFillRequestHandler(
                    autofillTypes = listOf(AutofillType.Username),
                    onFill = onUsernameChange
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = {
                        onUsernameChange(it)
                        if (it.isEmpty()) usernameAutoFillHandler.requestVerifyManual()
                    },
                    label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_username)) },
                    isError = uiState.isUsernameError,
                    supportingText = {
                        if (uiState.isUsernameError) {
                            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_username))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .connectNode(handler = usernameAutoFillHandler)
                        .defaultFocusChangeAutoFill(handler = usernameAutoFillHandler),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                val passwordAutoFillHandler = rememberAutoFillRequestHandler(
                    autofillTypes = listOf(AutofillType.Password),
                    onFill = onPasswordChange
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = {
                        onPasswordChange(it)
                        if (it.isEmpty()) passwordAutoFillHandler.requestVerifyManual()
                    },
                    isError = uiState.isPasswordError,
                    supportingText = {
                        if (uiState.isPasswordError) {
                            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_password))
                        }
                    },
                    label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_password)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .connectNode(handler = passwordAutoFillHandler)
                        .defaultFocusChangeAutoFill(handler = passwordAutoFillHandler),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview
@Composable
fun PreviewSmbServerInfoEditor() {
    ComicTheme {
        Surface {
            SmbServerInfoEditor()
        }
    }
}
