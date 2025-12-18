package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@ContributesTo(AppScope::class)
interface TutorialNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(toPair(TutorialNavKey.serializer()))

    @Provides
    @IntoSet
    private fun provideTutorialEntry(
        factory: TutorialScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<TutorialNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
            with(rememberRetained { factory.createTutorialScreenContext() }) {
                TutorialScreenRoot(onComplete = navigator::goBack)
            }
        }
    }
}

@Serializable
data object TutorialNavKey : ScreenKey
