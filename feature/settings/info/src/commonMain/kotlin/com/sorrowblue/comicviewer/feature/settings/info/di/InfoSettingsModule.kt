/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.di

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.navigation.infoSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.info.navigation.pdfPluginNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface InfoSettingsModule {
    @Provides
    @IntoSet
    private fun provideInfoSettingsEntry(): ScreenEntryProvider = { navigator ->
        infoSettingsNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideLicenseEntry(factory: LicenseScreenContext.Factory): ScreenEntryProvider =
        { navigator ->
            with(factory) {
                pdfPluginNavEntry(navigator)
            }
        }
}
