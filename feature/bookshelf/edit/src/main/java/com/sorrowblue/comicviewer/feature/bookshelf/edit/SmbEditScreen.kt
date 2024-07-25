package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.component.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.marginPadding
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
    val isProgress: Boolean = false,
) : BookshelfEditScreenUiState {

    constructor(smbServer: SmbServer, folder: Folder) : this(
        displayName = Input(smbServer.displayName),
        host = Input(smbServer.host),
        port = Input(smbServer.port.toString()),
        path = Input(folder.path.removePrefix("/").removeSuffix("/")),
        auth = when (smbServer.auth) {
            SmbServer.Auth.Guest -> Auth.Guest
            is SmbServer.Auth.UsernamePassword -> Auth.UserPass
        },
        domain = (smbServer.auth as? SmbServer.Auth.UsernamePassword)?.domain.orEmpty(),
        username = Input((smbServer.auth as? SmbServer.Auth.UsernamePassword)?.username.orEmpty()),
        password = Input((smbServer.auth as? SmbServer.Auth.UsernamePassword)?.password.orEmpty()),
        isError = false,
        isProgress = false
    )

    enum class Auth {
        Guest,
        UserPass,
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = uiState.editType.title)) },
                navigationIcon = { CloseIconButton(onClick = onBackClick) },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text(text = stringResource(id = R.string.bookshelf_edit_label_save))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
            scrollState = scrollState,
            contentPadding = contentPadding
        )
    }
    if (uiState.isProgress) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ComicTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable { }
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
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
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .drawVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .windowInsetsPadding(contentPadding.asWindowInsets())
            .marginPadding(dimension = ComicTheme.dimension, horizontal = true, bottom = true)
    ) {
        val dimension = ComicTheme.dimension
        DisplayNameField(
            input = uiState.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
        )

        HostField(
            input = uiState.host,
            onValueChange = onHostChange,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing)
        )

        PortField(
            input = uiState.port,
            onValueChange = onPortChange,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing)
        )

        PathField(
            input = uiState.path,
            auth = uiState.auth,
            onValueChange = onPathChange,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing)
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing * 2)
        )

        AuthButtons(
            currentAuth = uiState.auth,
            onAuthChange = onAuthChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing)
        )

        when (uiState.auth) {
            SmbEditScreenUiState.Auth.Guest -> Unit
            SmbEditScreenUiState.Auth.UserPass -> {
                DomainField(
                    value = uiState.domain,
                    onValueChange = onDomainChange,
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxWidth()
                        .padding(top = dimension.targetSpacing)
                )

                UsernameField(
                    input = uiState.username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxWidth()
                        .padding(top = dimension.targetSpacing)
                )

                PasswordField(
                    input = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxWidth()
                        .padding(top = dimension.targetSpacing)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewMultiScreen
private fun PreviewBookshelfEditScreen() {
    PreviewTheme {
        SmbEditScreen(
            uiState = SmbEditScreenUiState(auth = SmbEditScreenUiState.Auth.UserPass),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = { },
            onDisplayNameChange = { },
            onHostChange = { },
            onPortChange = { },
            onPathChange = { },
            onAuthChange = { },
            onDomainChange = { },
            onUsernameChange = { },
            onPasswordChange = { },
            onSaveClick = { }
        )
    }
}
