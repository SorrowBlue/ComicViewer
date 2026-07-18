/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class SmartCollectionEditNavKey(val collectionId: CollectionId) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun smartCollectionEditNavEntry(navigator: Navigator) {
    scope.entry<SmartCollectionEditNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        SmartCollectionEditScreenRoot(
            collectionId = it.collectionId,
            onCancelClick = navigator::goBack,
            onComplete = navigator::goBack,
        )
    }
}
