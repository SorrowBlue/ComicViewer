package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.AuthMethod
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditContentUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceStorageContent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbServerContent
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.add

sealed interface BookshelfEditScreenUiState {

    data object Loading : BookshelfEditScreenUiState

    data class Editing(
        val mode: EditMode = EditMode.Register,
        val running: Boolean = false,
        val editorUiState: BookshelfEditContentUiState = BookshelfEditContentUiState.SmbServer(),
    ) : BookshelfEditScreenUiState
}

enum class EditMode {
    Register,
    Change
}

sealed interface BookshelfEditUiEvent {
    data class ShowSnackbar(val message: String) : BookshelfEditUiEvent
    data object Complete : BookshelfEditUiEvent
}

@Composable
internal fun BookshelfEditRoute(
    args: BookshelfEditArgs,
    onBackClick: () -> Unit = {},
    onComplete: () -> Unit = {},
) {
//    BookshelfEditScreen(
//        uiState = state.uiState,
//        snackbarHostState = state.snackbarHostState,
//        onBackClick = onBackClick,
//        onDisplayNameChange = state::onDisplayNameChanged,
//        onSelectFolderClick = state::onSelectFolderClick,
//        onSaveClick = state::save
//    )
}

@Composable
fun rememberCompact(): Boolean {
    val windowSizeClass: WindowSizeClass = LocalWindowSize.current
    return remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
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
//            BookshelfEditAppBar(uiState, onBackClick, scrollBehavior)
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
                    is BookshelfEditContentUiState.DeviceStorage ->
                        Surface(
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                            shape = ComicTheme.shapes.large
                        ) {
                            DeviceStorageContent(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(ComicTheme.dimension.padding * 4),
                                uiState = uiState.editorUiState,
                                onDisplayNameChange = onDisplayNameChange,
                                onSelectFolderClick = onSelectFolderClick,
                                onSaveClick = onSaveClick
                            )
                        }

                    is BookshelfEditContentUiState.SmbServer ->
                        SmbServerContent(
                            padding = padding,
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
