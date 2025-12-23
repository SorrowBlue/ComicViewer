package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(factory: FileInfoScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.searchFileInfoNavEntry(navigator: Navigator) {
    with(factory) {
        fileInfoEntry<SearchFileInfoNavKey>(
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
}
