/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.nav.di

import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookNavModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(BookNavKey.serializer().asEntry())
}
