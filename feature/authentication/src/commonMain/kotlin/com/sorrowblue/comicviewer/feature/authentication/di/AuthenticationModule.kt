/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication.di

import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AuthenticationModule {

    @Provides
    @IntoSet
    private fun provideAuthenticationNavEntry(): ScreenEntryProvider = { navigator ->
        authenticationNavEntry(navigator)
    }
}
