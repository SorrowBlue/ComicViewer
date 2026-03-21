package com.sorrowblue.comicviewer.feature.settings.display.navigation

import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface DisplaySettingsProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        DisplaySettingsNavKey.serializer().asEntry(),
        DarkModeNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideDisplaySettingsEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            displaySettingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideDarkModeEntry(
        factory: DarkModeScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            darkModeNavEntry(navigator)
        }
    }
}
