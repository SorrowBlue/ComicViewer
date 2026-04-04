package com.sorrowblue.comicviewer.feature.settings.plugin.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object PdfPluginNavKey : NavKey

context(factory: PdfPluginScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.pdfPluginNavEntry(navigator: Navigator) {
    entry<PdfPluginNavKey>(metadata = DialogSceneStrategy.dialog()) {
        with(rememberRetained { factory.createPdfPluginScreenContext() }) {
            PdfPluginScreenRoot(onBackClick = navigator::goBack)
        }
    }
}
