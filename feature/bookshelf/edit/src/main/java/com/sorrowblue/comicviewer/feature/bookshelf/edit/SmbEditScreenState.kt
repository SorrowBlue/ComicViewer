package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SmbEditScreenState(
    uiState: SmbEditScreenUiState,
    val snackbarHostState: SnackbarHostState,
    private val args: BookshelfEditArgs,
    private val context: Context,
    private val scope: CoroutineScope,
    private val softwareKeyboardController: SoftwareKeyboardController?,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
) : BookshelfEditInnerScreenState<SmbEditScreenUiState>() {

    override var uiState by mutableStateOf(uiState)

    fun onDisplayNameChange(text: String) {
        uiState = uiState.copy(displayName = Input(value = text, isError = text.isBlank()))
    }

    fun onHostChange(text: String) {
        uiState = uiState.copy(host = Input(value = text, isError = text.isBlank()))
    }

    fun onPortChange(text: String) {
        uiState = uiState.copy(port = Input(value = text, isError = !portRegex.matches(text)))
    }

    fun onPathChange(text: String) {
        uiState = uiState.copy(path = uiState.path.copy(value = text))
    }

    fun onAuthChange(auth: SmbEditScreenUiState.Auth) {
        uiState = uiState.copy(auth = auth)
    }

    fun onDomainChange(text: String) {
        uiState = uiState.copy(domain = text)
    }

    fun onUsernameChange(text: String) {
        uiState = uiState.copy(username = Input(value = text, isError = text.isBlank()))
    }

    fun onPasswordChange(text: String) {
        uiState = uiState.copy(password = Input(value = text, isError = text.isBlank()))
    }

    fun onSaveClick(complete: () -> Unit) {
        softwareKeyboardController?.hide()
        onDisplayNameChange(uiState.displayName.value)
        onHostChange(uiState.host.value)
        onPortChange(uiState.port.value)
        onPathChange(uiState.path.value)
        var isError =
            uiState.displayName.isError || uiState.host.isError || uiState.port.isError || uiState.path.isError
        if (uiState.auth == SmbEditScreenUiState.Auth.UserPass) {
            onDomainChange(uiState.domain)
            onUsernameChange(uiState.username.value)
            onPasswordChange(uiState.password.value)
            isError = isError || uiState.username.isError || uiState.password.isError
        }
        uiState = uiState.copy(isError = isError)
        if (uiState.isError) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_msg_input_error))
            }
            return
        }
        uiState = uiState.copy(isProgress = true)
        val smbServer = SmbServer(
            id = args.bookshelfId,
            displayName = uiState.displayName.value,
            host = uiState.host.value,
            auth = when (uiState.auth) {
                SmbEditScreenUiState.Auth.Guest -> SmbServer.Auth.Guest
                SmbEditScreenUiState.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                    domain = uiState.domain,
                    username = uiState.username.value,
                    password = uiState.password.value
                )
            },
            port = uiState.port.value.toInt(),
        )
        scope.launch {
            registerBookshelfUseCase(
                RegisterBookshelfUseCase.Request(
                    smbServer,
                    if (uiState.path.value.isEmpty()) {
                        "/"
                    } else {
                        ("/${uiState.path.value}/").replace("(/+)".toRegex(), "/")
                    }
                )
            ).collectLatest {
                when (it) {
                    is Resource.Error -> {
                        uiState = uiState.copy(isProgress = false, isError = true)
                        scope.launch {
                            when (it.error) {
                                RegisterBookshelfUseCase.Error.Auth -> snackbarHostState.showSnackbar(
                                    "Authentication method is incorrect."
                                )

                                RegisterBookshelfUseCase.Error.Host -> snackbarHostState.showSnackbar(
                                    "サーバーに接続できません。\nサーバー名/IPアドレス、ポート番号を確認してください"
                                )

                                RegisterBookshelfUseCase.Error.Network -> snackbarHostState.showSnackbar(
                                    "ネットワークに接続できませんでした。"
                                )

                                RegisterBookshelfUseCase.Error.Path -> snackbarHostState.showSnackbar(
                                    "Pathが間違っています。"
                                )

                                RegisterBookshelfUseCase.Error.System -> snackbarHostState.showSnackbar(
                                    "システムエラー"
                                )
                            }
                        }
                    }

                    is Resource.Success -> {
                        uiState = uiState.copy(isProgress = false, isError = false)
                        complete()
                    }
                }
            }
        }
    }
}

private val portRegex =
    "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{1,5})|([0-9]{1,4}))\$".toRegex()
