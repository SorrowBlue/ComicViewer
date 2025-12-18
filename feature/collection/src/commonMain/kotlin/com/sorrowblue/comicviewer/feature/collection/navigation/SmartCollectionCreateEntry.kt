package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: SmartCollectionCreateScreenContext.Factory)
fun EntryProviderScope<NavKey>.smartCollectionCreateEntry(navigator: Navigator) {
    entry<CollectionNavKey.SmartCreate>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        with(rememberRetained { factory.createSmartCollectionCreateScreenContext() }) {
            SmartCollectionCreateScreenRoot(
                bookshelfId = it.bookshelfId,
                searchCondition = it.searchCondition,
                onCancelClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
    entry<SmartCollectionCreateNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        with(rememberRetained { factory.createSmartCollectionCreateScreenContext() }) {
            SmartCollectionCreateScreenRoot(
                bookshelfId = it.bookshelfId,
                searchCondition = it.searchCondition,
                onCancelClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}
