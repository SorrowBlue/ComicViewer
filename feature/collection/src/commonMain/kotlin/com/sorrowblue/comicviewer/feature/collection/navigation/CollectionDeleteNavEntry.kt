package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: DeleteCollectionScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.deleteCollectionNavEntry(navigator: Navigator) {
    entry<CollectionDeleteNavKey>(
        metadata = DialogSceneStrategy.dialog() + NavDisplay.transitionMaterialFadeThrough(),
    ) {
        with(rememberRetained { factory.createDeleteCollectionScreenContext() }) {
            DeleteCollectionScreenRoot(
                id = it.id,
                onBackClick = navigator::goBack,
                onComplete = {
                    navigator.pop<CollectionListNavKey>(false)
                },
            )
        }
    }
}
