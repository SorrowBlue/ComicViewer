/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import kotlinx.serialization.Serializable

internal const val SCENE_KEY_COLLECTION = "Collection"

@Serializable
internal data class CollectionNavKey(val id: CollectionId) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun collectionNavEntry(navigator: Navigator) {
    scope.entry<CollectionNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane(SCENE_KEY_COLLECTION) +
            NavDisplay.transitionMaterialSharedAxisZ(),
    ) { detail ->
        CollectionScreenRoot(
            id = detail.id,
            onBackClick = dropUnlessResumed {
                navigator.pop<CollectionNavKey>(inclusive = true)
            },
            onFileClick = dropUnlessResumed { file ->
                when (file) {
                    is Book -> {
                        navigator.navigate(
                            BookNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                                name = file.name,
                                collectionId = detail.id,
                            ),
                        )
                    }

                    is Folder -> {
                        navigator.popNavigate<CollectionFileInfoNavKey>(
                            FolderNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                            ),
                        )
                    }
                }
            },
            onFileInfoClick = dropUnlessResumed { file ->
                navigator.popNavigate<CollectionFileInfoNavKey>(
                    CollectionFileInfoNavKey(file.key()),
                )
            },
            onEditClick = dropUnlessResumed { collection ->
                navigator.navigate(
                    when (collection) {
                        is BasicCollection -> BasicCollectionEditNavKey(collection.id)
                        is SmartCollection -> SmartCollectionEditNavKey(collection.id)
                    },
                )
            },
            onDeleteClick = dropUnlessResumed { id ->
                navigator.navigate(CollectionDeleteNavKey(id))
            },
            onSettingsClick = dropUnlessResumed {
                navigator.navigate(SettingsNavKey)
            },
        )
    }
}
