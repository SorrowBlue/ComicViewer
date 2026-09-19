/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.add.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun basicCollectionAddNavEntry(navigator: Navigator) {
    scope.entry<BasicCollectionAddNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        BasicCollectionAddScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            onBackClick = dropUnlessResumed(block = navigator::goBack),
            onCollectionCreateClick = dropUnlessResumed { id, path ->
                navigator.navigate(BasicCollectionCreateNavKey(id, path))
            },
        )
    }
}
