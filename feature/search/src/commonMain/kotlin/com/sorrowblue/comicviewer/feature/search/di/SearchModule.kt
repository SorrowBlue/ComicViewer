/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.di

import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.feature.search.navigation.searchFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.search.navigation.searchFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.search.navigation.searchNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SearchModule {
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
    private fun provideSearchFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        searchFileInfoNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideSearchFolderFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        searchFolderFileInfoNavEntry(navigator)
    }
}
