package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenRoot
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: CollectionScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.collectionNavEntry(navigator: Navigator) {
    entry<CollectionNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane("Collection") +
            NavDisplay.transitionMaterialSharedAxisZ(),
    ) { detail ->
        with(rememberRetained { factory.createCollectionScreenContext() }) {
            CollectionScreenRoot(
                id = detail.id,
                onBackClick = navigator::goBack,
                onFileClick = { file ->
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
                            navigator.navigate(
                                CollectionFolderNavKey(
                                    bookshelfId = file.bookshelfId,
                                    path = file.path,
                                ),
                            )
                        }
                    }
                },
                onFileInfoClick = {
                    navigator.navigate(CollectionFileInfoNavKey(it.key()))
                },
                onEditClick = {
                    navigator.navigate(
                        when (it) {
                            is BasicCollection -> BasicCollectionEditNavKey(it.id)
                            is SmartCollection -> SmartCollectionEditNavKey(it.id)
                        },
                    )
                },
                onDeleteClick = { navigator.navigate(CollectionDeleteNavKey(it)) },
                onSettingsClick = { navigator.navigate(SettingsNavKey) },
            )
        }
    }
}
