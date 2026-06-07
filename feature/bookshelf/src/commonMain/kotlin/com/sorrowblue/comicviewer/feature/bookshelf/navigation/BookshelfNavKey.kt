package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.BookshelfInfoNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.Shelves
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object BookshelfNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.bookshelf_label_bookshelf)

    override val icon get() = ComicIcons.Shelves

    override val order get() = 1
}

internal fun EntryProviderScope<NavKey>.bookshelfNavEntry(navigator: Navigator) {
    entry<BookshelfNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane<BookshelfInfoNavKey>("Bookshelf") +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        BookshelfScreenRoot(
            onSettingsClick = {
                navigator.navigate(SettingsNavKey)
            },
            onFabClick = {
                navigator.navigate(BookshelfWizardNavKey.Selection)
            },
            onBookshelfClick = { id, path ->
                navigator.navigate(BookshelfFolderNavKey(id, path))
            },
            onBookshelfInfoClick = { bookshelfFolder ->
                navigator.popNavigate<BookshelfInfoNavKey>(
                    BookshelfInfoNavKey(bookshelfFolder.bookshelf.id),
                )
            },
        )
    }
}
