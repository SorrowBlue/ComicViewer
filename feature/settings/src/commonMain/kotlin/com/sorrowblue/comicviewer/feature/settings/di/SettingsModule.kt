/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.di

import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.navigation.inAppLanguagePickerNavEntry
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsModule {
    @Provides
    @IntoSet
    private fun provideSettingsNavEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            settingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideInAppLanguagePickerNavEntry(
        factory: InAppLanguagePickerScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            inAppLanguagePickerNavEntry(navigator)
        }
    }
}
