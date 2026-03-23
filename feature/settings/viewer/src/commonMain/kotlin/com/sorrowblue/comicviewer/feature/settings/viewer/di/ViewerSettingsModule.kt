package com.sorrowblue.comicviewer.feature.settings.viewer.di

import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.viewerSettingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface ViewerSettingsModule {
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
