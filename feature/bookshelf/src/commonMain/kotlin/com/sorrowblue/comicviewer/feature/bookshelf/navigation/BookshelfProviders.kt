package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookshelfProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        BookshelfNavKey.serializer().asEntry(),
        BookshelfFolderNavKey.serializer().asEntry(),
        BookshelfFolderFileInfoNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = BookshelfNavKey

    @Provides
    @IntoSet
    private fun provideBookshelfNavEntry(
        factory: BookshelfScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            bookshelfNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                bookshelfFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
