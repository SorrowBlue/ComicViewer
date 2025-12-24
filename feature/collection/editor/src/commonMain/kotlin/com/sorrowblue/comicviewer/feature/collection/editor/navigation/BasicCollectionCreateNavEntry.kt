package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenRoot
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
