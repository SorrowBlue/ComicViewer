package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthButtons
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DomainField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PasswordField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.SaveButton
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.SnackbarHostState
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class SmbEditScreenState(
    uiState: SmbEditScreenUiState,
    val snackbarHostState: SnackbarHostState,
    private val args: BookshelfEditArgs,
    private val viewModel: BookshelfEditViewModel,
    private val scope: CoroutineScope,
) : BookshelfEditInnerScreenState<SmbEditScreenUiState>() {

    override var uiState by mutableStateOf(uiState)

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
        uiState = uiState.copy(domain = text)
    }

    fun onUsernameChange(text: String) {
        uiState = uiState.copy(username = Input(value = text, isError = text.isBlank()))
    }

    fun onPasswordChange(text: String) {
        uiState = uiState.copy(password = Input(value = text, isError = text.isBlank()))
    }

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

@Parcelize
data class SmbEditScreenUiState(
    val editType: EditType = EditType.Register,
    val displayName: Input = Input(),
    val host: Input = Input(),
    val port: Input = Input("445"),
    val path: Input = Input(),
    val auth: Auth = Auth.Guest,
    val domain: String = "",
    val username: Input = Input(),
    val password: Input = Input(),
    val isError: Boolean = false,
    val isProgress: Boolean = true,
) : BookshelfEditScreenUiState {

    constructor(smbServer: SmbServer, folder: Folder) : this(
        displayName = Input(smbServer.displayName),
        host = Input(smbServer.host),
        port = Input(smbServer.port.toString()),
        path = Input(folder.path.removeSurrounding("/")),
        auth = when (smbServer.auth) {
            SmbServer.Auth.Guest -> Auth.Guest
            is SmbServer.Auth.UsernamePassword -> Auth.UserPass
        },
        domain = (smbServer.auth as? SmbServer.Auth.UsernamePassword)?.domain.orEmpty(),
        username = Input((smbServer.auth as? SmbServer.Auth.UsernamePassword)?.username.orEmpty()),
        password = Input((smbServer.auth as? SmbServer.Auth.UsernamePassword)?.password.orEmpty()),
        isError = false,
        isProgress = true
    )

    enum class Auth {
        Guest,
        UserPass,
    }
}

@Composable
internal fun SmbEditRoute(
    state: SmbEditScreenState,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val uiState = state.uiState
    SmbEditScreen(
        uiState = uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onDisplayNameChange = state::onDisplayNameChange,
        onHostChange = state::onHostChange,
        onPortChange = state::onPortChange,
        onPathChange = state::onPathChange,
        onAuthChange = state::onAuthChange,
        onDomainChange = state::onDomainChange,
        onUsernameChange = state::onUsernameChange,
        onPasswordChange = state::onPasswordChange,
        onSaveClick = { state.onSaveClick(onComplete) }
    )
}

@Composable
private fun SmbEditScreen(
    uiState: SmbEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onPathChange: (String) -> Unit,
    onAuthChange: (SmbEditScreenUiState.Auth) -> Unit,
    onDomainChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.editType.title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        SmbEditContent(
            uiState = uiState,
            onDisplayNameChange = onDisplayNameChange,
            onHostChange = onHostChange,
            onPortChange = onPortChange,
            onPathChange = onPathChange,
            onAuthChange = onAuthChange,
            onDomainChange = onDomainChange,
            onUsernameChange = onUsernameChange,
            onPasswordChange = onPasswordChange,
            onSaveClick = onSaveClick,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun SmbEditContent(
    uiState: SmbEditScreenUiState,
    onDisplayNameChange: (String) -> Unit,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onPathChange: (String) -> Unit,
    onAuthChange: (SmbEditScreenUiState.Auth) -> Unit,
    onDomainChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        DisplayNameField(
            input = uiState.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        HostField(
            input = uiState.host,
            onValueChange = onHostChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding * 2))

        PortField(
            input = uiState.port,
            onValueChange = onPortChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        PathField(
            input = uiState.path,
            auth = uiState.auth,
            onValueChange = onPathChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        AuthButtons(currentAuth = uiState.auth, onAuthChange = onAuthChange)

        when (uiState.auth) {
            SmbEditScreenUiState.Auth.Guest -> Unit
            SmbEditScreenUiState.Auth.UserPass -> {
                DomainField(
                    value = uiState.domain,
                    onValueChange = onDomainChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(8.dp))

                UsernameField(
                    input = uiState.username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(8.dp))

                PasswordField(
                    input = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .weight(1f)
        )
        SaveButton(
            enabled = !uiState.isError,
            onClick = onSaveClick,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
