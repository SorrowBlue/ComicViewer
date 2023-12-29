package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class SmbEditScreenState(
    uiState: SmbEditScreenUiState,
    val snackbarHostState: SnackbarHostState,
    private val args: BookshelfEditArgs,
    private val viewModel: BookshelfEditViewModel,
    private val context: Context,
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
    contentPadding: PaddingValues,
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
        onSaveClick = { state.onSaveClick(onComplete) },
        contentPadding = contentPadding
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
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    ResponsiveDialogScaffold(
        title = { Text(text = stringResource(id = uiState.editType.title)) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentPadding = contentPadding,
        onSaveClick = onSaveClick,
        onCloseClick = onBackClick,
        scrollState = scrollState,
        modifier = modifier,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun ResponsiveDialogScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
    snackbarHost: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    widthSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable (PaddingValues) -> Unit,
) {
    if (widthSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || widthSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = ComicIcons.Close,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = onSaveClick) {
                            Text(text = "Save")
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = snackbarHost,
            contentWindowInsets = contentPadding.asWindowInsets(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            content(innerPadding.add(paddingValues = PaddingValues(16.dp)))
        }
    } else {
        BasicAlertDialog(onDismissRequest = onCloseClick) {
            AlertDialogContent(title = {
                TopAppBar(
                    title = title,
                    actions = {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = ComicIcons.Close,
                                contentDescription = null
                            )
                        }
                    }
                )
            }, text = {
                Box {
                    content(PaddingValues())
                    HorizontalDivider(
                        modifier = Modifier
                            .alpha(if (scrollState.canScrollBackward) 1f else 0f)
                            .align(Alignment.TopCenter)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .alpha(if (scrollState.canScrollForward) 1f else 0f)
                            .align(Alignment.BottomCenter)
                    )
                }
            }, buttons = {
                AlertDialogFlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 12.dp
                ) {
                    TextButton(onClick = onCloseClick) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick = onSaveClick) {
                        Text(text = "Save")
                    }
                }
            })
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
        modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(contentPadding)
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

@Composable
internal fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = ComicTheme.shapes.extraLarge,
    containerColor: Color = ComicTheme.colorScheme.surface,
    tonalElevation: Dp = ElevationTokens.Level3,
    buttonContentColor: Color = ComicTheme.colorScheme.primary,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            title?.let {
                title()
            }
            text?.let {
                Box(
                    Modifier
                        .weight(weight = 1f, fill = false)
                        .align(Alignment.Start)
                ) {
                    text()
                }
            }
            Box(modifier = Modifier.align(Alignment.End)) {
                val textStyle = ComicTheme.typography.labelLarge
                ProvideContentColorTextStyle(
                    contentColor = buttonContentColor,
                    textStyle = textStyle,
                    content = buttons
                )
            }
        }
    }
}

@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}

@Composable
internal fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() +
                placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            // Ensures that confirming actions appear above dismissive actions.
            @Suppress("ListIterator")
            sequences.add(0, currentSequence.toList())
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        measurables.fastForEach { measurable ->
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.fastForEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                        if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize,
                        childrenMainAxisSizes,
                        layoutDirection,
                        mainAxisPositions
                    )
                }
                placeables.fastForEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}
