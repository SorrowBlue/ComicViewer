/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class CollectionDeleteNavKey(val id: CollectionId) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun deleteCollectionNavEntry(navigator: Navigator) {
    scope.entry<CollectionDeleteNavKey>(
        metadata = DialogSceneStrategy.dialog() + NavDisplay.transitionMaterialFadeThrough(),
    ) {
        DeleteCollectionScreenRoot(
            id = it.id,
            onBackClick = navigator::goBack,
            onComplete = {
                navigator.pop<CollectionListNavKey>(false)
            },
        )
    }
}
