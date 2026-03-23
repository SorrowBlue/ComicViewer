package com.sorrowblue.comicviewer.feature.settings.nav.di

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(NavScope::class)
interface SettingsNavModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        setOf(SettingsNavKey.serializer().asEntry())
}
