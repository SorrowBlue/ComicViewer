package com.sorrowblue.comicviewer.feature.settings.plugin.di

import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.PdfPluginNavKey
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.PluginSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.pdfPluginNavEntry
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface PluginSettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        PluginSettingsNavKey.serializer().asEntry(),
        PdfPluginNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun providePluginSettingsEntry(
        factory: PluginScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            pdfPluginNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun providePdfPluginEntry(
        factory: PdfPluginScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            pdfPluginNavEntry(navigator)
        }
    }
}
