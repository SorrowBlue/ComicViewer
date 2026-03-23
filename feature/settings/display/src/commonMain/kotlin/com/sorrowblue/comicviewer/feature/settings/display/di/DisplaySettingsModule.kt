package com.sorrowblue.comicviewer.feature.settings.display.di

import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DarkModeNavKey
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.display.navigation.darkModeNavEntry
import com.sorrowblue.comicviewer.feature.settings.display.navigation.displaySettingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface DisplaySettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        DisplaySettingsNavKey.serializer().asEntry(),
        DarkModeNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideDisplaySettingsNavEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            displaySettingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideDarkModeNavEntry(
        factory: DarkModeScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            darkModeNavEntry(navigator)
        }
    }
}
