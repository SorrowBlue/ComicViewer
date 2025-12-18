package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
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
interface DisplaySettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(DisplaySettingsNavKey.serializer()),
            toPair(DarkModeNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideDisplaySettingsEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            displaySettingsNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideDarkModeEntry(
        factory: DarkModeScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            darkModeNavEntry(navigator)
        }
    }
}
