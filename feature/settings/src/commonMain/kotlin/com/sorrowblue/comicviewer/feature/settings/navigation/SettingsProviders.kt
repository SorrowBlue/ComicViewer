package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface SettingsProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(SettingsNavKey.serializer()),
            toPair(ImageCacheNavKey.serializer()),
            toPair(InAppLanguagePickerNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideSettingsNavEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            settingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideImageCacheNavEntry(
        factory: ImageCacheScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            imageCacheNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideInAppLanguagePickerNavEntry(
        factory: InAppLanguagePickerScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            inAppLanguagePickerNavEntry(navigator)
        }
    }
}
