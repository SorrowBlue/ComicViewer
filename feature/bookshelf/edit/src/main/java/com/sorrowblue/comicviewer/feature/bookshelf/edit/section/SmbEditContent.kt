package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

data class SmbEditScreenUiState(
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
) {

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
internal fun MobileSmbEditContent(
    state: SmbEditContentState,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MobileSmbEditContent(
        uiState = state.uiState,
        onDisplayNameChange = state::onDisplayNameChange,
        onHostChange = state::onHostChange,
        onPortChange = state::onPortChange,
        onPathChange = state::onPathChange,
        onAuthChange = state::onAuthChange,
        onDomainChange = state::onDomainChange,
        onUsernameChange = state::onUsernameChange,
        onPasswordChange = state::onPasswordChange,
        onSaveClick = { state.onSaveClick(onComplete) },
        modifier = modifier
    )
}

@Composable
private fun MobileSmbEditContent(
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

@Composable
internal fun TabletSmbEditContent(
    state: SmbEditContentState,
    modifier: Modifier = Modifier,
) {
    TabletSmbEditContent(
        uiState = state.uiState,
        onDisplayNameChange = state::onDisplayNameChange,
        onHostChange = state::onHostChange,
        onPortChange = state::onPortChange,
        onPathChange = state::onPathChange,
        onAuthChange = state::onAuthChange,
        onDomainChange = state::onDomainChange,
        onUsernameChange = state::onUsernameChange,
        onPasswordChange = state::onPasswordChange,
        modifier = modifier
    )
}

@Composable
private fun TabletSmbEditContent(
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
    }
}

@Preview
@Composable
private fun PreviewTabletSmbEditContent() {
    PreviewTheme {
        Surface {
            TabletSmbEditContent(
                SmbEditScreenUiState(auth = SmbEditScreenUiState.Auth.UserPass),
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMobileSmbEditContent() {
    PreviewTheme {
        Surface {
            MobileSmbEditContent(
                SmbEditScreenUiState(auth = SmbEditScreenUiState.Auth.UserPass),
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
            )
        }
    }
}
