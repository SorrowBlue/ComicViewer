package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BookshelfFolderNavKey(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String? = null,
    @Transient override val onRestoreComplete: (() -> Unit)? = null,
) : FolderNavKey {
    override val showSearch = true
}

@Serializable
internal data class BookshelfFolderFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}

context(
    factoryFolder: FolderScreenContext.Factory,
    factoryFileInfo: FileInfoScreenContext.Factory,
)
internal fun EntryProviderScope<NavKey>.bookshelfFolderFileInfoNavEntry(navigator: Navigator) {
    with(factoryFolder) {
        with(factoryFileInfo) {
            folderFileInfoNavEntry<BookshelfFolderNavKey, BookshelfFolderFileInfoNavKey>(
                sceneKeyPrefix = "Bookshelf",
                onBackClick = {
                    navigator.pop<BookshelfFolderNavKey>(inclusive = true)
                },
                onInfoBackClick = {
                    navigator.goBack()
                },
                onSearchClick = { id, path ->
                    navigator.navigate(SearchNavKey(id, path))
                },
                onFileClick = { file ->
                    when (file) {
                        is Book -> {
                            navigator.navigate(
                                BookNavKey(
                                    bookshelfId = file.bookshelfId,
                                    path = file.path,
                                    name = file.name,
                                ),
                            )
                        }

                        is Folder -> {
                            navigator.navigate<BookshelfFolderFileInfoNavKey>(
                                BookshelfFolderNavKey(
                                    bookshelfId = file.bookshelfId,
                                    path = file.path,
                                ),
                                inclusive = true,
                            )
                        }
                    }
                },
                onFileInfoClick = {
                    navigator.navigate<BookshelfFolderFileInfoNavKey>(
                        BookshelfFolderFileInfoNavKey(
                            it.key(),
                        ),
                        inclusive = true,
                    )
                },
                onSettingsClick = {
                    navigator.navigate(SettingsNavKey)
                },
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
            )
        }
    }
}
