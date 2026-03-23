package com.sorrowblue.comicviewer.feature.settings.info.di

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.navigation.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.info.navigation.LicenseNavKey
import com.sorrowblue.comicviewer.feature.settings.info.navigation.infoSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.info.navigation.pdfPluginNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface InfoSettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        InfoSettingsNavKey.serializer().asEntry(),
        LicenseNavKey.serializer().asEntry(),
    )

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
