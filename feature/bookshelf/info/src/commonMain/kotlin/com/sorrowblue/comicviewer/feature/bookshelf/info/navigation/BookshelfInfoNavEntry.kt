package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BookshelfInfoScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookshelfInfoNavEntry(
    navigator: Navigator,
    sceneKey: String,
) {
    entry<BookshelfInfoNavKey>(
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey = sceneKey) +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createBookshelfInfoScreenContext() }) {
            BookshelfInfoScreenRoot(
                bookshelfId = it.id,
                onBackClick = navigator::goBack,
                onRemoveClick = { navigator.navigate(BookshelfDeleteNavKey(it.id)) },
                showNotificationPermissionRationale = { scanType ->
                    navigator.navigate(BookshelfNotificationNavKey(scanType))
                },
                onEditClick = { id, type ->
                    navigator.navigate(
                        BookshelfEditNavKey(BookshelfEditType.Edit(id, type)),
                    )
                },
            )
        }
    }
}
