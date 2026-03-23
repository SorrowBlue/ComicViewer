package com.sorrowblue.comicviewer.feature.history.di

import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryClearAllNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryFileInfoNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryFolderFileInfoNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryFolderNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.historyClearAllNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyNavEntry
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
interface HistoryModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        HistoryNavKey.serializer().asEntry(),
        HistoryFileInfoNavKey.serializer().asEntry(),
        HistoryFolderFileInfoNavKey.serializer().asEntry(),
        HistoryClearAllNavKey.serializer().asEntry(),
        HistoryFolderNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideNavigationKey(): NavigationKey = HistoryNavKey

    @Provides
    @IntoSet
    private fun provideHistoryNavEntry(
        factory: HistoryScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            historyNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryClearAllNavEntry(): ScreenEntryProvider = { navigator ->
        historyClearAllNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideHistoryFileInfoNavEntry(
        factory: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            historyFileInfoNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideHistoryFolderFileInfoNavEntry(
        factoryFolder: FolderScreenContext.Factory,
        factoryFileInfo: FileInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factoryFolder) {
            with(factoryFileInfo) {
                historyFolderFileInfoNavEntry(navigator)
            }
        }
    }
}
