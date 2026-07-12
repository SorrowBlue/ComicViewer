/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

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
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
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

internal fun EntryProviderScope<NavKey>.bookshelfFolderFileInfoNavEntry(navigator: Navigator) {
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
                    navigator.popNavigate<BookshelfFolderFileInfoNavKey>(
                        BookshelfFolderNavKey(
                            bookshelfId = file.bookshelfId,
                            path = file.path,
                        ),
                    )
                }
            }
        },
        onFileInfoClick = {
            navigator.popNavigate<BookshelfFolderFileInfoNavKey>(
                BookshelfFolderFileInfoNavKey(
                    it.key(),
                ),
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
