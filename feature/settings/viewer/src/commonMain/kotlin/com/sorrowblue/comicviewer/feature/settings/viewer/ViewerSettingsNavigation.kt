package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface ViewerSettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (ViewerSettingsNavKey::class as KClass<NavKey>) to (ViewerSettingsNavKey.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideViewerSettingsEntry(
        factory: ViewerSettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<ViewerSettingsNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings")
                + NavDisplay.transitionMaterialSharedAxisX()
        ) {
            with(rememberRetained { factory.createViewerSettingsScreenContext() }) {
                ViewerSettingsScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
