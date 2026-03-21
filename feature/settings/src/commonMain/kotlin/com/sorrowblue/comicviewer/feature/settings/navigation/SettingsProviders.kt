package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        SettingsNavKey.serializer().asEntry(),
        ImageCacheNavKey.serializer().asEntry(),
        InAppLanguagePickerNavKey.serializer().asEntry(),
    )

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
    private fun provideImageCacheNavEntry(
        factory: ImageCacheScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            imageCacheNavEntry(navigator)
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
