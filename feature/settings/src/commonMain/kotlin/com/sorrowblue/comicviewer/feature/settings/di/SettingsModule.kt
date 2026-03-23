package com.sorrowblue.comicviewer.feature.settings.di

import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.navigation.ImageCacheNavKey
import com.sorrowblue.comicviewer.feature.settings.navigation.InAppLanguagePickerNavKey
import com.sorrowblue.comicviewer.feature.settings.navigation.imageCacheNavEntry
import com.sorrowblue.comicviewer.feature.settings.navigation.inAppLanguagePickerNavEntry
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface SettingsModule {
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
