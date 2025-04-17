package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarAction
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContents
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContentsAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.navigation.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import com.sorrowblue.comicviewer.domain.model.collection.Collection as CollectionModel

@Serializable
internal data class Collection(val id: CollectionId)

interface CollectionScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun onCollectionEditClick(collection: CollectionModel)
    fun onFileClick(file: File, collectionId: CollectionId)
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
    fun onOpenFolderClick(file: File)
}

@Destination<Collection>
@Composable
internal fun CollectionScreen(
    route: Collection,
    navigator: CollectionScreenNavigator = koinInject(),
) {
    val state = rememberCollectionScreenState(route)
    CollectionScreen(
        navigator = state.navigator,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        uiState = state.uiState,
        lazyGridState = state.lazyGridState,
        snackbarHostState = state.snackbarHostState,
        onAppBarAction = state::onAppBarAction,
        onSheetAction = state::onSheetAction,
        onContentsAction = state::onContentsAction,
    )
    val currentNavigator by rememberUpdatedState(navigator)
    EventEffect(state.events) {
        when (it) {
            CollectionScreenEvent.Back -> currentNavigator.navigateUp()
            is CollectionScreenEvent.CollectionAddClick ->
                currentNavigator.onCollectionAddClick(it.bookshelfId, it.path)

            is CollectionScreenEvent.FileClick -> currentNavigator.onFileClick(it.file, route.id)
            is CollectionScreenEvent.OpenFolderClick ->
                currentNavigator.onOpenFolderClick(it.file)

            CollectionScreenEvent.SettingsClick -> currentNavigator.onSettingsClick()
            is CollectionScreenEvent.EditClick -> currentNavigator.onCollectionEditClick(it.collection)
        }
    }
    NavTabHandler(onClick = state::onNavClick)
}

internal data class SmartCollectionScreenUiState(
    val appBarUiState: CollectionAppBarUiState = CollectionAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)

@Composable
internal fun CollectionScreen(
    navigator: ThreePaneScaffoldNavigator<File.Key>,
    lazyPagingItems: LazyPagingItems<File>,
    uiState: SmartCollectionScreenUiState,
    lazyGridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    onAppBarAction: (CollectionAppBarAction) -> Unit,
    onSheetAction: (FileInfoSheetNavigator) -> Unit,
    onContentsAction: (CollectionContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        topBar = {
            CollectionAppBar(
                uiState = uiState.appBarUiState,
                onAction = onAppBarAction,
                scrollBehavior = scrollBehavior,
                scrollableState = lazyGridState
            )
        },
        extraPane = { contentKey ->
            FileInfoSheet(
                fileKey = contentKey,
                onAction = onSheetAction,
                isOpenFolderEnabled = true
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        navigator = navigator,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        CollectionContents(
            fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            contentPadding = contentPadding,
            onAction = onContentsAction,
        )
    }
}
