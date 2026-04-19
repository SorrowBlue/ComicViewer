package com.sorrowblue.comicviewer.feature.settings.extension.di

import com.sorrowblue.comicviewer.feature.settings.extension.navigation.ExtensionSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.ImageCacheNavKey
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.PdfPluginNavKey
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.PluginSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.extensionSettingsNavEntry
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.imageCacheNavEntry
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.pdfPluginNavEntry
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface ExtensionSettingsModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        ExtensionSettingsNavKey.serializer().asEntry(),
        ImageCacheNavKey.serializer().asEntry(),
        PluginSettingsNavKey.serializer().asEntry(),
        PdfPluginNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideExtensionSettingsNavEntry(): ScreenEntryProvider = { navigator ->
        extensionSettingsNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideImageCacheNavEntry(
        factory: ImageCacheScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            imageCacheNavEntry(navigator)
        }
    }

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
