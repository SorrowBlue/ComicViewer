package com.sorrowblue.comicviewer.feature.settings.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface SettingsNavProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        setOf(SettingsNavKey.serializer().asEntry<SettingsNavKey>())
}
