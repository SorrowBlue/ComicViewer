package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.AuthMethod
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceStorageInfoEditor
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbServerInfoEditor
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.flow.CollectAsEffect
import com.sorrowblue.comicviewer.framework.ui.responsive.FullScreenTopAppBar
import kotlinx.coroutines.launch

sealed interface BookshelfEditScreenUiState {

    data object Loading : BookshelfEditScreenUiState

    data object Complete : BookshelfEditScreenUiState

    data class Editing(
        val mode: EditMode = EditMode.Register,
        val running: Boolean = false,
        val editorUiState: BookshelfEditorUiState = BookshelfEditorUiState.SmbServer(),
    ) : BookshelfEditScreenUiState
}

enum class EditMode {
    Register,
    Change
}

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
}

@Composable
internal fun BookshelfEditRoute(
    viewModel: BookshelfEditViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onComplete: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                viewModel.updateUri(uri)
            } ?: run {
                scope.launch {
                    snackbarHostState.showSnackbar("フォルダを選択してください")
                }
            }
        }
    val openDocumentTreeIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
    }
    viewModel.uiEvents.CollectAsEffect {
        when (it) {
            is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(it.message)
        }
    }
    BookshelfEditScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onDisplayNameChange = viewModel::onDisplayNameChanged,
        onSelectFolderClick = { activityResultLauncher.launch(openDocumentTreeIntent) },
        onHostChange = viewModel::onHostChanged,
        onPortChange = viewModel::onPortChanged,
        onPathChange = viewModel::onPathChange,
        onAuthMethodChange = viewModel::onAuthMethodChange,
        onDomainChange = viewModel::onDomainChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSaveClick = viewModel::save
    )
    if (uiState is BookshelfEditScreenUiState.Complete) {
        onComplete()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfEditScreen(
    uiState: BookshelfEditScreenUiState = BookshelfEditScreenUiState.Editing(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
    onDisplayNameChange: (String) -> Unit = {},
    onSelectFolderClick: () -> Unit = {},
    onHostChange: (String) -> Unit = {},
    onPortChange: (String) -> Unit = {},
    onPathChange: (String) -> Unit = {},
    onAuthMethodChange: (AuthMethod) -> Unit = {},
    onDomainChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    val windowSizeClass: WindowSizeClass = LocalWindowSize.current
    val isCompact = remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FullScreenTopAppBar(
                title = {
                    Text(
                        text = when (uiState) {
                            BookshelfEditScreenUiState.Complete -> ""
                            is BookshelfEditScreenUiState.Editing -> when (uiState.mode) {
                                EditMode.Register -> "本棚の登録"
                                EditMode.Change -> "本棚の編集"
                            }

                            BookshelfEditScreenUiState.Loading -> ""
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
    ) { contentPaddings ->
        val padding = if (isCompact) {
            contentPaddings
                .add(
                    PaddingValues(
                        start = ComicTheme.dimension.margin,
                        end = ComicTheme.dimension.margin,
                        bottom = ComicTheme.dimension.margin
                    )
                )
        } else {
            contentPaddings
                .add(
                    paddingValues = PaddingValues(
                        start = ComicTheme.dimension.margin,
                        end = ComicTheme.dimension.margin,
                        top = ComicTheme.dimension.spacer,
                        bottom = ComicTheme.dimension.margin,
                    )
                )
        }
        when (uiState) {
            is BookshelfEditScreenUiState.Editing ->
                when (uiState.editorUiState) {
                    is BookshelfEditorUiState.DeviceStorage ->
                        Surface(
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                            shape = ComicTheme.shapes.large
                        ) {
                            DeviceStorageInfoEditor(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(ComicTheme.dimension.padding * 4),
                                uiState = uiState.editorUiState,
                                isRunning = uiState.running,
                                onDisplayNameChange = onDisplayNameChange,
                                onSelectFolderClick = onSelectFolderClick,
                                onSaveClick = onSaveClick
                            )
                        }

                    is BookshelfEditorUiState.SmbServer ->
                        SmbServerInfoEditor(
                            padding = padding,
                            modifier = Modifier,
                            uiState = uiState.editorUiState,
                            onDisplayNameChange = onDisplayNameChange,
                            onHostChange = onHostChange,
                            onPortChange = onPortChange,
                            onPathChange = onPathChange,
                            onAuthMethodChange = onAuthMethodChange,
                            onDomainChange = onDomainChange,
                            onUsernameChange = onUsernameChange,
                            onPasswordChange = onPasswordChange,
                            onSaveClick = onSaveClick
                        )
                }

            is BookshelfEditScreenUiState.Complete -> {}

            BookshelfEditScreenUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(contentPaddings)
                ) {
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewBookshelfEditScreen() {
    ComicTheme {
        Surface {
            BookshelfEditScreen()
        }
    }
}

@Preview
@Composable
fun PreviewBookshelfEditScreenLoading() {
    ComicTheme {
        Surface {
            BookshelfEditScreen(BookshelfEditScreenUiState.Loading)
        }
    }
}
