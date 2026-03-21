package com.sorrowblue.comicviewer.feature.settings.plugin.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenRoot
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained

@ContributesTo(AppScope::class)
interface PluginSettingsProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        PluginSettingsNavKey.serializer().asEntry(),
        PdfPluginNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun providePluginSettingsEntry(
        factory: PluginScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
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
    ): ScreenEntryProvider = { navigator ->
        entry<PdfPluginNavKey>(metadata = DialogSceneStrategy.dialog()) {
            with(rememberRetained { factory.createPdfPluginScreenContext() }) {
                PdfPluginScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
