package com.sorrowblue.comicviewer.feature.settings.security.di

import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.securitySettingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface SecuritySettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(SecuritySettingsNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideSecuritySettingsEntry(
        factory: SecuritySettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            securitySettingsNavEntry(navigator)
        }
    }
}
