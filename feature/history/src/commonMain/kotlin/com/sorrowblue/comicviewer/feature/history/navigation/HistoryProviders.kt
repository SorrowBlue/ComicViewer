package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
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
interface HistoryProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        HistoryNavKey.serializer().asEntry<HistoryNavKey>(),
        HistoryFileInfoNavKey.serializer().asEntry<HistoryFileInfoNavKey>(),
        HistoryFolderFileInfoNavKey.serializer().asEntry<HistoryFolderFileInfoNavKey>(),
        HistoryClearAllNavKey.serializer().asEntry<HistoryClearAllNavKey>(),
        HistoryFolderNavKey.serializer().asEntry<HistoryFolderNavKey>(),
    )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = HistoryNavKey

    @Provides
    @IntoSet
    private fun provideHistoryNavEntry(
        factory: HistoryScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factory) {
            historyNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryClearAllNavEntry(): ScreenEntryProvider = {
        historyClearAllNavEntry(it)
    }

    @Provides
    @IntoSet
    private fun provideHistoryFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factory) {
            historyFileInfoNavEntry(it)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factoryFolder) {
            with(factoryFileInfo) {
                historyFolderFileInfoNavEntry(it)
            }
        }
    }
}
