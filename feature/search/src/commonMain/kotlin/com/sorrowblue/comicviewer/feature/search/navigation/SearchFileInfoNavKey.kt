/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry2
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun searchFileInfoNavEntry(navigator: Navigator) {
    fileInfoEntry2<SearchFileInfoNavKey>(
        "Search",
        onBackClick = navigator::goBack,
        onCollectionClick = {
            navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
        },
        onOpenFolderClick = {
            navigator.navigate(
                SearchFolderNavKey(it.bookshelfId, it.parent, it.path),
            )
        },
    )
}
