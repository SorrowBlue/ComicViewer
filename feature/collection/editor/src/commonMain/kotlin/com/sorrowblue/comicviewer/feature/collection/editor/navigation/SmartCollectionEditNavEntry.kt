package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

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
