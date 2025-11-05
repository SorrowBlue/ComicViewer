package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenRoot
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenRoot
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import kotlinx.serialization.Serializable

interface FolderKey : ScreenKey {
    val bookshelfId: BookshelfId
    val path: String
    val restorePath: String?
}

interface FileInfoKey : ScreenKey {
    val fileKey: File.Key
    val isOpenFolderEnabled: Boolean
}

@Serializable
data class SortTypeSelectKey(val sortType: SortType, val folderScopeOnly: Boolean) : ScreenKey

context(graph: PlatformGraph)
inline fun <reified T : FolderKey, reified V : FileInfoKey> EntryProviderScope<NavKey>.folderEntryGroup(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onSearchClick: (BookshelfId, PathString) -> Unit,
    noinline onFileClick: (File) -> Unit,
    noinline onFileInfoClick: (File) -> Unit,
    noinline onSortClick: (SortType, Boolean) -> Unit,
    noinline onRestored: () -> Unit,
    noinline onCollectionClick: (File) -> Unit,
    noinline onOpenFolderClick: (File) -> Unit,
) {
    folderEntry<T>(
        "${sceneKey}Folder",
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        onSortClick = onSortClick,
        onRestored = onRestored
    )
    fileInfoEntry<V>(
        "${sceneKey}Folder",
        onBackClick = onBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick
    )
}

context(graph: PlatformGraph)
inline fun <reified T : FileInfoKey> EntryProviderScope<NavKey>.fileInfoEntry(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onCollectionClick: (File) -> Unit,
    noinline onOpenFolderClick: (File) -> Unit,
) {
    entryScreen<T, FileInfoScreenContext>(
        createContext = { (graph as FileInfoScreenContext.Factory).createFileInfoScreenContext() },
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey)
    ) {
        FileInfoScreenRoot(
            fileKey = it.fileKey,
            isOpenFolderEnabled = it.isOpenFolderEnabled,
            onBackClick = onBackClick,
            onCollectionClick = onCollectionClick,
            onOpenFolderClick = onOpenFolderClick,
        )
    }
}

context(graph: PlatformGraph)
inline fun <reified T : FolderKey> EntryProviderScope<NavKey>.folderEntry(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onSearchClick: (BookshelfId, PathString) -> Unit,
    noinline onFileClick: (File) -> Unit,
    noinline onFileInfoClick: (File) -> Unit,
    noinline onSortClick: (SortType, Boolean) -> Unit,
    noinline onRestored: () -> Unit,
) {
    entryScreen<T, FolderScreenContext>(
        clazzContentKey = { it.toString() },
        createContext = { (graph as FolderScreenContext.Factory).createFolderScreenContext() },
        metadata = SupportingPaneSceneStrategy.mainPane(sceneKey) +
            NavigationResultMetadata.resultConsumer(SortTypeSelectScreenResultKey)
    ) {
        FolderScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            restorePath = it.restorePath,
            onBackClick = onBackClick,
            onSearchClick = { onSearchClick(it.bookshelfId, it.path) },
            onFileClick = onFileClick,
            onFileInfoClick = onFileInfoClick,
            onSortClick = onSortClick,
            onRestored = onRestored
        )
    }
}

context(graph: PlatformGraph)
fun EntryProviderScope<NavKey>.sortTypeSelectEntry(onBackClick: () -> Unit) {
    entry<SortTypeSelectKey>(metadata = DialogSceneStrategy.dialog()) {
        SortTypeSelectScreenRoot(it.sortType, it.folderScopeOnly, onDismissRequest = onBackClick)
    }
}
