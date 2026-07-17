/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.folder.navigation.folderFileInfoNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class ReadLaterFolderNavKey(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String? = null,
) : FolderNavKey

@Serializable
internal data class ReadLaterFolderFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun readLaterFolderFileInfoNavEntry(navigator: Navigator) {
    folderFileInfoNavEntry<ReadLaterFolderNavKey, ReadLaterFolderFileInfoNavKey>(
        sceneKeyPrefix = "ReadLater",
        onBackClick = {
            navigator.pop<ReadLaterFolderNavKey>(inclusive = true)
        },
        onInfoBackClick = {
            navigator.goBack()
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
                    navigator.popNavigate<ReadLaterFolderFileInfoNavKey>(
                        ReadLaterFolderNavKey(
                            bookshelfId = file.bookshelfId,
                            path = file.path,
                        ),
                    )
                }
            }
        },
        onFileInfoClick = {
            navigator.popNavigate<ReadLaterFolderFileInfoNavKey>(
                ReadLaterFolderFileInfoNavKey(
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
