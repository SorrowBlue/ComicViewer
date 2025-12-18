package com.sorrowblue.comicviewer.feature.settings.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data object SettingsNavKey : ScreenKey

@ContributesTo(AppScope::class)
interface SettingsNav {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(toPair(SettingsNavKey.serializer()))
}
