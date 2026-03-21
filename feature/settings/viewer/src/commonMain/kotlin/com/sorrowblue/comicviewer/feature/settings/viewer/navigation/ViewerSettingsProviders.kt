package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ViewerSettingsProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(ViewerSettingsNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideViewerSettingsEntry(
        factory: ViewerSettingsScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            viewerSettingsNavEntry(navigator)
        }
    }
}
