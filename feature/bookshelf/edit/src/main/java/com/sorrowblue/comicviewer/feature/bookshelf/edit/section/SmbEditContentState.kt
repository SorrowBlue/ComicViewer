package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.annotation.CallSuper
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditViewModel
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface SmbEditContentState : BookshelfEditContentState {

    var uiState: SmbEditScreenUiState

    val snackbarHostState: SnackbarHostState

    fun onDisplayNameChange(text: String) {
        uiState = uiState.copy(displayName = Input(value = text, isError = text.isBlank()))
    }

    fun onHostChange(text: String) {
        uiState = uiState.copy(host = Input(value = text, isError = !hostRegex.matches(text)))
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
        uiState = uiState.copy(domain = uiState.domain)
    }

    fun onUsernameChange(text: String) {
        uiState = uiState.copy(username = Input(value = text, isError = text.isBlank()))
    }

    fun onPasswordChange(text: String) {
        uiState = uiState.copy(password = Input(value = text, isError = text.isBlank()))
    }

    @CallSuper
    fun onSaveClick(complete: () -> Unit) {
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
    }
}

internal class SmbEditContentStateImpl(
    uiState: SmbEditScreenUiState,
    private val args: BookshelfEditArgs,
    override val snackbarHostState: SnackbarHostState,
    private val viewModel: BookshelfEditViewModel,
    private val scope: CoroutineScope,
) : SmbEditContentState {

    override var uiState: SmbEditScreenUiState by mutableStateOf(uiState)

    override fun onSaveClick(complete: () -> Unit) {
        super.onSaveClick(complete)
        if (uiState.isError) {
            scope.launch {
                snackbarHostState.showSnackbar("Please check your entries.")
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
        viewModel.save(
            smbServer,
            if (uiState.path.value.isEmpty()) {
                "/"
            } else {
                ("/${uiState.path.value}/").replace("(/+)".toRegex(), "/")
            }
        ) {
            uiState = uiState.copy(isProgress = false)
            complete()
        }
    }
}

private val portRegex =
    "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{1,5})|([0-9]{1,4}))\$".toRegex()

private val hostRegex =
    "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\$".toRegex()
