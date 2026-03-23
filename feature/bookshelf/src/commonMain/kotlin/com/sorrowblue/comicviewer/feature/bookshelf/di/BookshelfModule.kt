package com.sorrowblue.comicviewer.feature.bookshelf.di

import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfFolderFileInfoNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfFolderNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.bookshelfFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.bookshelfNavEntry
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface BookshelfModule {
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
