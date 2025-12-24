package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

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
