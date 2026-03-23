package com.sorrowblue.comicviewer.feature.search.di

import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.feature.search.navigation.SearchFileInfoNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.SearchFolderFileInfoNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.SearchFolderNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavKey
import com.sorrowblue.comicviewer.feature.search.navigation.searchFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.search.navigation.searchFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.search.navigation.searchNavEntry
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface SearchModule {
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
    private fun provideSearchNavEntry(factory: SearchScreenContext.Factory): ScreenEntryProvider =
        { navigator ->
            with(factory) {
                searchNavEntry(navigator)
            }
        }

    @Provides
    @IntoSet
    private fun provideSearchFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            searchFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSearchFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                searchFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
