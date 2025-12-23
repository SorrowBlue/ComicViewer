package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfSelectionNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.BookshelfInfoNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import io.github.takahirom.rin.rememberRetained

context(factory: BookshelfScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookshelfNavEntry(navigator: Navigator) {
    entry<BookshelfNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane<BookshelfInfoNavKey>("Bookshelf") +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        with(rememberRetained { factory.createBookshelfScreenContext() }) {
            BookshelfScreenRoot(
                onSettingsClick = { navigator.navigate(SettingsNavKey) },
                onFabClick = { navigator.navigate(BookshelfSelectionNavKey) },
                onBookshelfClick = { id, path ->
                    navigator.navigate(BookshelfFolderNavKey(id, path))
                },
                onBookshelfInfoClick = {
                    navigator.navigate(BookshelfInfoNavKey(it.bookshelf.id))
                },
            )
        }
    }
}
