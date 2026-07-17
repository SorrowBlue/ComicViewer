/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.di

import com.sorrowblue.comicviewer.feature.book.navigation.receiveBookNavEntry
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ReceiveBookModule {
    @Provides
    @IntoSet
    private fun provideReceiveBookNavEntry(
        factory: ReceiveBookScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            receiveBookNavEntry(navigator)
        }
    }
}
