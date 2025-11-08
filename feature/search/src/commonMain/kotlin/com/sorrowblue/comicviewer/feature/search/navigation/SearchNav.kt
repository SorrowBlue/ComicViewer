package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.feature.search.SearchScreenRoot
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.FolderKey
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import com.sorrowblue.comicviewer.folder.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.navigation.folderEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val SearchKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SearchKey.List::class, SearchKey.List.serializer())
        subclass(SearchKey.FileInfo::class, SearchKey.FileInfo.serializer())
        subclass(SearchKey.FileInfo2::class, SearchKey.FileInfo2.serializer())
        subclass(SearchKey.Folder::class, SearchKey.Folder.serializer())
    }
}

@Serializable
sealed interface SearchKey : ScreenKey {
    @Serializable
    data class List(val bookshelfId: BookshelfId, val path: PathString) : SearchKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        SearchKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class FileInfo2(override val fileKey: File.Key) :
        SearchKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data class Folder(override val bookshelfId: BookshelfId, override val path: String) :
        SearchKey,
        FolderKey {
        override val restorePath: String? = null
    }
}

context(graph: PlatformGraph, appNavigationState: AppNavigationState)
fun EntryProviderScope<NavKey>.searchEntryGroup(
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, PathString) -> Unit,
    onBookClick: (Book) -> Unit,
    onCollectionClick: (File) -> Unit,
    onSmartCollectionClick: (BookshelfId, SearchCondition) -> Unit,
) {
    searchEntry(
        onBackClick = appNavigationState::onBackPressed,
        onSettingsClick = onSettingsClick,
        onSmartCollectionClick = onSmartCollectionClick,
        onFileClick = {
            when (it) {
                is Book -> onBookClick(it)
                is Folder -> appNavigationState.addToBackStack(
                    SearchKey.Folder(it.bookshelfId, it.path),
                )
            }
        },
        onFileInfoClick = {
            appNavigationState.addToBackStack(SearchKey.FileInfo(it.key()))
        },
    )

    searchFileInfoEntry(
        onBackClick = appNavigationState::onBackPressed,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = {
            appNavigationState.addToBackStack(
                SearchKey.Folder(it.bookshelfId, it.parent),
            )
        },
    )

    folderEntryGroup<SearchKey.Folder, SearchKey.FileInfo2>(
        "Search",
        onBackClick = appNavigationState::onBackPressed,
        onSearchClick = onSearchClick,
        onFileClick = { file ->
            when (file) {
                is Book -> onBookClick(file)
                is Folder -> appNavigationState.addToBackStack(
                    SearchKey.Folder(file.bookshelfId, file.path),
                )
            }
        },
        onFileInfoClick = {
            appNavigationState.addToBackStack(SearchKey.FileInfo2(it.key()))
        },
        onRestored = { /* Do noting */ },
        onSortClick = { sortType, folderScopeOnly ->
            appNavigationState.addToBackStack(SortTypeSelectKey(sortType, folderScopeOnly))
        },
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = {
            appNavigationState.addToBackStack(
                SearchKey.Folder(it.bookshelfId, it.parent),
            )
        },
    )
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.searchEntry(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSmartCollectionClick: (BookshelfId, SearchCondition) -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
) {
    entryScreen<SearchKey.List, SearchScreenContext>(
        createContext = { (graph as SearchScreenContext.Factory).createSearchScreenContext() },
        metadata = SupportingPaneSceneStrategy.mainPane("Search"),
    ) {
        SearchScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onSmartCollectionClick = onSmartCollectionClick,
            onFileClick = onFileClick,
            onFileInfoClick = onFileInfoClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.searchFileInfoEntry(
    onBackClick: () -> Unit,
    onCollectionClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    fileInfoEntry<SearchKey.FileInfo>(
        "Search",
        onBackClick = onBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick,
    )
}
