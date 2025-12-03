package com.sorrowblue.comicviewer.feature.settings.plugin.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenRoot
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val PluginSettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(PluginSettingsKey::class, PluginSettingsKey.serializer())
        subclass(PdfPluginKey::class, PdfPluginKey.serializer())
    }
}

@Serializable
data object PluginSettingsKey : ScreenKey

@Serializable
private data object PdfPluginKey : ScreenKey

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.pluginSettingsEntryGroup(navigator: Navigator) {
    pluginSettingsEntry(
        onBackClick = navigator::goBack,
        onPdfPluginClick = { navigator.navigate(PdfPluginKey) },
    )
    pdfPluginEntry(onBackClick = navigator::goBack)
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.pluginSettingsEntry(
    onBackClick: () -> Unit,
    onPdfPluginClick: () -> Unit,
) {
    entryScreen<PluginSettingsKey, PluginScreenContext>(
        createContext = {
            context.require<PluginScreenContext.Factory>().createPluginScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane(
            "Settings",
        ) + NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        PluginScreenRoot(
            onBackClick = onBackClick,
            onPdfPluginClick = onPdfPluginClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.pdfPluginEntry(onBackClick: () -> Unit) {
    entryScreen<PdfPluginKey, PdfPluginScreenContext>(
        createContext = {
            context.require<PdfPluginScreenContext.Factory>().createPdfPluginScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        PdfPluginScreenRoot(
            onBackClick = onBackClick,
        )
    }
}
