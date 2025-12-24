package com.sorrowblue.comicviewer.feature.collection.add.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenContext
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BasicCollectionAddScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.basicCollectionAddNavEntry(navigator: Navigator) {
    entry<BasicCollectionAddNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        with(rememberRetained { factory.createBasicCollectionAddScreenContext() }) {
            BasicCollectionAddScreenRoot(
                bookshelfId = it.bookshelfId,
                path = it.path,
                onBackClick = navigator::goBack,
                onCollectionCreateClick = { id, path ->
                    navigator.navigate(BasicCollectionCreateNavKey(id, path))
                },
            )
        }
    }
}
