package com.sorrowblue.comicviewer.favorite

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteArgs
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FavoriteRoute(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: FavoriteScreenState = rememberFavoriteScreenState(),
) {
    val uiState = state.uiState
    val navigator = state.navigator
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    FavoriteScreen(
        navigator = navigator,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onEditClick = { onEditClick(state.favoriteId) },
        onFileListTypeChange = state::toggleFileListType,
        onGridSizeChange = state::toggleGridSize,
        onDeleteClick = { state.delete(onBackClick) },
        onSettingsClick = onSettingsClick,
        onFileClick = { onClickFile(it, state.favoriteId) },
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = lazyGridState,
        contentPadding = contentPadding,
        onFavoriteClick = onFavoriteClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onOpenFolderClick = onOpenFolderClick,
        onReadLaterClick = state::onReadLaterClick,
    )
}

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
@Stable
internal class FavoriteScreenState(
    savedStateHandle: SavedStateHandle,
    private val args: FavoriteArgs,
    val navigator: ThreePaneScaffoldNavigator,
    private val scope: CoroutineScope,
    private val viewModel: FavoriteViewModel,
) {

    init {
        viewModel.displaySettings.map(FolderDisplaySettings::toFileContentLayout)
            .distinctUntilChanged().onEach {
                uiState = uiState.copy(
                    favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(fileContentType = it),
                    fileContentType = it
                )
            }.launchIn(scope)
        scope.launch {
            viewModel.getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
                .collectLatest {
                    if (it.dataOrNull != null) {
                        uiState =
                            uiState.copy(
                                favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(title = it.dataOrNull!!.name)
                            )
                    }
                }
        }
    }

    fun delete(onBackClick: () -> Unit) {
        viewModel.delete(favoriteId, onBackClick)
    }

    fun toggleGridSize() {
        if (uiState.fileContentType is FileContentType.Grid) {
            viewModel.updateGridSize()
        }
    }

    fun onFileInfoClick(file: File) {
        uiState = uiState.copy(file = file)
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.fileContentType) {
                is FileContentType.Grid -> FolderDisplaySettings.Display.LIST
                FileContentType.List -> FolderDisplaySettings.Display.GRID
            }
        )
    }

    fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    fun onReadLaterClick(file: File) {
        viewModel.addToReadLater(file)
    }

    val favoriteId: FavoriteId get() = args.favoriteId
    val pagingDataFlow = viewModel.pagingDataFlow(args.favoriteId)
    var uiState: FavoriteScreenUiState by savedStateHandle.saveable {
        mutableStateOf(
            FavoriteScreenUiState()
        )
    }
        private set
}

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFavoriteScreenState(
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteViewModel = hiltViewModel(),
): FavoriteScreenState {
    return remember {
        FavoriteScreenState(
            savedStateHandle = savedStateHandle,
            args = FavoriteArgs(arguments!!),
            navigator = navigator,
            scope = scope,
            viewModel = viewModel,
        )
    }
}

@Parcelize
internal data class FavoriteScreenUiState(
    val file: File? = null,
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileContentType: FileContentType = FileContentType.List,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    navigator: ThreePaneScaffoldNavigator,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListTypeChange: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onGridSizeChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    lazyGridState: LazyGridState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        topBar = {
            FavoriteAppBar(
                uiState = uiState.favoriteAppBarUiState,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onFileListTypeChange = onFileListTypeChange,
                onGridSizeChange = onGridSizeChange,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { innerPadding ->
            val file = uiState.file
            if (file != null) {
                FileInfoSheet(
                    file = file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(file) },
                    onFavoriteClick = { onFavoriteClick(file) },
                    onOpenFolderClick = { onOpenFolderClick(file) },
                    contentPadding = innerPadding
                )
            }
        },
        navigator = navigator,
        contentPadding = contentPadding,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val dimension = LocalDimension.current
        val inInnerPadding = innerPadding.add(
            PaddingValues(
                start = dimension.margin,
                top = dimension.margin,
                end = dimension.margin,
                bottom = dimension.margin
            )
        )
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(id = R.string.favorite_label_no_favorites),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inInnerPadding)
            )
        } else {
            FileContent(
                type = uiState.fileContentType,
                lazyPagingItems = lazyPagingItems,
                contentPadding = inInnerPadding,
                onFileClick = onFileClick,
                onInfoClick = onFileInfoClick,
                state = lazyGridState
            )
        }
    }
}
