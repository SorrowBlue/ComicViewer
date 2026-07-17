/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history.di

import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.navigation.historyClearAllNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.history.navigation.historyNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface HistoryModule {
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
    private fun provideHistoryFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        historyFileInfoNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideHistoryFolderFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        historyFolderFileInfoNavEntry(navigator)
    }
}
