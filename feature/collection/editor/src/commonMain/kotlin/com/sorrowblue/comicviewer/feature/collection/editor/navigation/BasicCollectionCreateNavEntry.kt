package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BasicCollectionCreateScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.basicCollectionCreateNavEntry(navigator: Navigator) {
    entry<BasicCollectionCreateNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        with(rememberRetained { factory.createBasicCollectionCreateScreenContext() }) {
            BasicCollectionCreateScreenRoot(
                bookshelfId = it.bookshelfId,
                path = it.path,
                onBackClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}

context(factory: BasicCollectionEditScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.basicCollectionEditNavEntry(navigator: Navigator) {
    entry<BasicCollectionEditNavKey>(
        metadata = DialogSceneStrategy.dialog(
            DialogProperties(usePlatformDefaultWidth = false),
        ),
    ) {
        with(rememberRetained { factory.createBasicCollectionEditScreenContext() }) {
            BasicCollectionEditScreenRoot(
                collectionId = it.collectionId,
                onBackClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}

context(factory: SmartCollectionCreateScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.smartCollectionCreateNavEntry(navigator: Navigator) {
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

context(factory: SmartCollectionEditScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.smartCollectionEditNavEntry(navigator: Navigator) {
    entry<SmartCollectionEditNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        with(rememberRetained { factory.createSmartCollectionEditScreenContext() }) {
            SmartCollectionEditScreenRoot(
                collectionId = it.collectionId,
                onCancelClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}
