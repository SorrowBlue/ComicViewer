package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfInfoNavKey(val id: BookshelfId) : NavKey

internal fun EntryProviderScope<NavKey>.bookshelfInfoNavEntry(
    navigator: Navigator,
    sceneKey: String,
) {
    entry<BookshelfInfoNavKey>(
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey = sceneKey) +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        BookshelfInfoScreenRoot(
            bookshelfId = it.id,
            onBackClick = navigator::goBack,
            onRemoveClick = {
                navigator.navigate(BookshelfDeleteNavKey(it.id))
            },
            showNotificationPermissionRationale = { scanType ->
                navigator.navigate(BookshelfNotificationNavKey(scanType))
            },
            onEditClick = { id, type ->
                navigator.navigate(BookshelfWizardNavKey.Edit(id, type))
            },
        )
    }
}
