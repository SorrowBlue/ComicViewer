package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(
    factoryFolder: FolderScreenContext.Factory,
    factoryFileInfo: FileInfoScreenContext.Factory,
)
internal fun EntryProviderScope<NavKey>.bookshelfFolderFileInfoNavEntry(navigator: Navigator) {
    with(factoryFolder) {
        with(factoryFileInfo) {
            folderFileInfoNavEntry<BookshelfFolderNavKey, BookshelfFolderFileInfoNavKey>(
                sceneKeyPrefix = "Bookshelf",
                onBackClick = navigator::goBack,
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
                            if (navigator.backStack.lastOrNull() is BookshelfFolderFileInfoNavKey) {
                                navigator.goBack()
                            }
                            navigator.navigate(
                                BookshelfFolderNavKey(file.bookshelfId, file.path),
                            )
                        }
                    }
                },
                onFileInfoClick = {
                    navigator.navigate(BookshelfFolderFileInfoNavKey(it.key()))
                },
                onSettingsClick = { navigator.navigate(SettingsNavKey) },
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
            )
        }
    }
}
