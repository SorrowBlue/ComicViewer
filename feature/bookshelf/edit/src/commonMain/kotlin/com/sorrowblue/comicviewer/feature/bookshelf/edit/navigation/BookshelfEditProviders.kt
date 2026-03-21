package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookshelfEditProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        BookshelfWizardNavKey.Selection.serializer().asEntry(),
        BookshelfWizardNavKey.Edit.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideBookshelfWizardNavEntry(
        factory: BookshelfEditScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            bookshelfWizardNavEntry(navigator)
        }
    }
}
