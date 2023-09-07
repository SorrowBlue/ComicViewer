package com.sorrowblue.comicviewer.settings.security.section

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.settings.security.R

sealed interface PasswordDialogUiState {

    data object Hide : PasswordDialogUiState
    data class Activation(val password: String = "") : PasswordDialogUiState
    data class Change(
        val oldPassword: String = "",
        val newPassword: String = "",
        val error: Int? = null
    ) :
        PasswordDialogUiState

    data class Invalidation(val password: String = "", val error: Int? = null) :
        PasswordDialogUiState
}

@Composable
fun PasswordDialog(
    uiState: PasswordDialogUiState = PasswordDialogUiState.Change(error = R.string.settings_security_password_manage_dialog_error_passwords_do_not_match),
    onPasswordChange: (String) -> Unit = {},
    onOldPasswordChange: (String) -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    if (uiState == PasswordDialogUiState.Hide) return
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = onConfirmClick) {
                Text(text = "Confirm")
            }
        },
        title = {
            when (uiState) {
                is PasswordDialogUiState.Activation -> Text(text = stringResource(id = R.string.settings_security_password_manage_dialog_title_password_setting))
                is PasswordDialogUiState.Change -> Text(text = stringResource(id = R.string.settings_security_password_manage_dialog_title_change_password))
                is PasswordDialogUiState.Invalidation -> Text(text = stringResource(id = R.string.settings_security_password_manage_dialog_title_delete_password))
                PasswordDialogUiState.Hide -> Unit
            }
        },
        text = {
            Column {
                when (uiState) {
                    is PasswordDialogUiState.Activation -> {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            label = {
                                Text(text = "Password")
                            }
                        )
                    }

                    is PasswordDialogUiState.Change -> {
                        OutlinedTextField(
                            value = uiState.oldPassword,
                            onValueChange = onOldPasswordChange,
                            label = {
                                Text(text = stringResource(id = R.string.settings_security_password_manage_dialog_hint_old_password))
                            },
                            isError = uiState.error != null,
                            supportingText = {
                                if (uiState.error != null) {
                                    Text(text = stringResource(id = uiState.error))
                                }
                            }
                        )
                        OutlinedTextField(
                            value = uiState.newPassword,
                            onValueChange = onPasswordChange,
                            label = {
                                Text(text = stringResource(id = R.string.settings_security_password_manage_dialog_hint_new_password))
                            }
                        )
                    }

                    is PasswordDialogUiState.Invalidation -> {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            label = {
                                Text(text = "Password")
                            },
                            isError = uiState.error != null,
                            supportingText = {
                                if (uiState.error != null) {
                                    Text(text = stringResource(id = uiState.error))
                                }
                            }
                        )
                    }

                    PasswordDialogUiState.Hide -> Unit
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewPasswordDialogActivation() {
    AppMaterialTheme {
        PasswordDialog(PasswordDialogUiState.Activation())
    }
}

@Preview
@Composable
private fun PreviewPasswordDialog() {
    AppMaterialTheme {
        PasswordDialog(PasswordDialogUiState.Change(error = R.string.settings_security_password_manage_dialog_error_passwords_do_not_match))
    }
}

@Preview
@Composable
private fun PreviewPasswordDialogInvalidation() {
    AppMaterialTheme {
        PasswordDialog(PasswordDialogUiState.Invalidation())
    }
}
