package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ResponsiveDialogScaffold
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.parcelize.Parcelize

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
internal fun SmbEditScreen(
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

@OptIn(ExperimentalMaterial3Api::class)
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
    scrollState: ScrollState = rememberScrollState(),
) {
    ResponsiveDialogScaffold(
        title = { Text(text = stringResource(id = uiState.editType.title)) },
        onCloseClick = onBackClick,
        confirmButton = {
            TextButton(onClick = onSaveClick) {
                Text(text = "Save")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        scrollableState = scrollState,
        modifier = modifier
    ) { innerPadding ->
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
            scrollState = scrollState,
            contentPadding = innerPadding
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
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(contentPadding)
            .drawVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
    ) {
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
    }
}
