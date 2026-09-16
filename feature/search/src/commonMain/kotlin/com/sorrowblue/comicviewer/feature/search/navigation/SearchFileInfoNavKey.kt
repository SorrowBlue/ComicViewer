/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import com.sorrowblue.comicviewer.feature.file.nav.fileInfoEntry
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun searchFileInfoNavEntry(navigator: Navigator) {
    fileInfoEntry<SearchFileInfoNavKey>(
        "Search",
        onBackClick = navigator::goBack,
        onCollectionClick = {
            navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
        },
        onOpenFolderClick = {
            navigator.navigate(
                FolderNavKey(it.bookshelfId, it.parent, it.path),
            )
        },
    )
}
