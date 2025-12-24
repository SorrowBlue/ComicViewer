package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(factory: FileInfoScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.collectionFileInfoNavEntry(navigator: Navigator) {
    with(factory) {
        fileInfoEntry<CollectionFileInfoNavKey>(
            sceneKey = "Collection",
            onBackClick = navigator::goBack,
            onCollectionClick = {
                navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
            },
            onOpenFolderClick = {
                navigator.navigate(CollectionFolderNavKey(it.bookshelfId, it.parent, it.path))
            },
        )
    }
}
