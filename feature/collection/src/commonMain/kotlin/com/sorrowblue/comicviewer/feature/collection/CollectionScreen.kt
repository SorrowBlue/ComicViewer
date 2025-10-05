package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldLayout
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.EventEffect
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
        scaffoldState = state.scaffoldState,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        uiState = state.uiState,
        lazyGridState = state.lazyGridState,
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
}

internal data class SmartCollectionScreenUiState(
    val appBarUiState: CollectionAppBarUiState = CollectionAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)

@Composable
internal fun CollectionScreen(
    scaffoldState: CanonicalScaffoldState<File.Key>,
    lazyPagingItems: LazyPagingItems<File>,
    uiState: SmartCollectionScreenUiState,
    lazyGridState: LazyGridState,
    onAppBarAction: (CollectionAppBarAction) -> Unit,
    onSheetAction: (FileInfoSheetNavigator) -> Unit,
    onContentsAction: (CollectionContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    scaffoldState.appBarState.scrollBehavior = scrollBehavior
    scaffoldState.CanonicalScaffoldLayout(
        topBar = {
            CollectionAppBar(
                uiState = uiState.appBarUiState,
                onAction = onAppBarAction,
            )
        },
        extraPane = { contentKey ->
            FileInfoSheet(
                fileKey = contentKey,
                onAction = onSheetAction,
                isOpenFolderEnabled = true
            )
        },
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
