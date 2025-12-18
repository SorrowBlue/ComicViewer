package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface PluginSettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(PluginSettingsNavKey.serializer()),
            toPair(PdfPluginNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun providePluginSettingsEntry(
        factory: PluginScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<PluginSettingsNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings") +
                NavDisplay.transitionMaterialSharedAxisX(),
        ) {
            with(rememberRetained { factory.createPluginScreenContext() }) {
                PluginScreenRoot(
                    onBackClick = navigator::goBack,
                    onPdfPluginClick = { navigator.navigate(PdfPluginNavKey) },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun providePdfPluginEntry(
        factory: PdfPluginScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<PdfPluginNavKey>(metadata = DialogSceneStrategy.dialog()) {
            with(rememberRetained { factory.createPdfPluginScreenContext() }) {
                PdfPluginScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
