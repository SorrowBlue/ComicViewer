package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: CollectionListScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.collectionListNavEntry(navigator: Navigator) {
    entry<CollectionListNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        with(rememberRetained { factory.createCollectionListScreenContext() }) {
            CollectionListScreenRoot(
                onItemClick = { navigator.navigate(CollectionNavKey(it.id)) },
                onEditClick = {
                    navigator.navigate(
                        when (it) {
                            is BasicCollection ->
                                BasicCollectionEditNavKey(it.id)

                            is SmartCollection ->
                                SmartCollectionEditNavKey(it.id)
                        },
                    )
                },
                onDeleteClick = {
                    navigator.navigate(CollectionNavKey(it.id))
                },
                onSettingsClick = {
                    navigator.navigate(SettingsNavKey)
                },
                onCreateBasicCollectionClick = {
                    navigator.navigate(BasicCollectionCreateNavKey())
                },
                onCreateSmartCollectionClick = {
                    navigator.navigate(SmartCollectionCreateNavKey())
                },
            )
        }
    }
}
