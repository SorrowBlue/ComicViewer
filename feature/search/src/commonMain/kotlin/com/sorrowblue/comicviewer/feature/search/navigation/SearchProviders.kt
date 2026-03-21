package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SearchProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        SearchNavKey.serializer().asEntry(),
        SearchFileInfoNavKey.serializer().asEntry(),
        SearchFolderFileInfoNavKey.serializer().asEntry(),
        SearchFolderNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideSearchNavEntry(
        factory: SearchScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factory) {
            searchNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideSearchFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factory) {
            searchFileInfoNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideSearchFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factoryFolder) {
            with(factoryFileInfo) {
                searchFolderFileInfoNavEntry(it)
            }
        }
    }
}
