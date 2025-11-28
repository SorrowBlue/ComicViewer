package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val ViewerSettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(ViewerSettingsKey::class, ViewerSettingsKey.serializer())
    }
}

@Serializable
data object ViewerSettingsKey : ScreenKey

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.viewerSettingsEntryGroup(navigator: Navigator) {
    viewerSettingsEntry(onBackClick = navigator::goBack)
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.viewerSettingsEntry(onBackClick: () -> Unit) {
    entryScreen<ViewerSettingsKey, ViewerSettingsScreenContext>(
        createContext = {
            context.require<ViewerSettingsScreenContext.Factory>()
                .createViewerSettingsScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        ViewerSettingsScreenRoot(onBackClick = onBackClick)
    }
}
