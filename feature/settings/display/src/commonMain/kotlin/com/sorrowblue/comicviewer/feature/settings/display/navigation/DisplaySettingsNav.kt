package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val DisplaySettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(DisplaySettingsKey::class, DisplaySettingsKey.serializer())
        subclass(DarkModeKey::class, DarkModeKey.serializer())
    }
}

@Serializable
data object DisplaySettingsKey : ScreenKey

@Serializable
private data object DarkModeKey : ScreenKey

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.displaySettingsEntryGroup(navigator: Navigator) {
    displaySettingsEntry(
        onBackClick = navigator::goBack,
        onDarkModeClick = { navigator.navigate(DarkModeKey) },
    )
    darkModeEntry(
        onDismissRequest = navigator::goBack,
        onComplete = navigator::goBack,
    )
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.displaySettingsEntry(
    onBackClick: () -> Unit,
    onDarkModeClick: () -> Unit,
) {
    entryScreen<DisplaySettingsKey, DisplaySettingsScreenContext>(
        createContext = {
            context.require<DisplaySettingsScreenContext.Factory>()
                .createDisplaySettingsScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        DisplaySettingsScreenRoot(
            onBackClick = onBackClick,
            onDarkModeClick = onDarkModeClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.darkModeEntry(
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<DarkModeKey, DarkModeScreenContext>(
        createContext = {
            context.require<DarkModeScreenContext.Factory>().createDarkModeScreenContext()
        },
        metadata = DialogSceneStrategy.dialog(),
    ) {
        DarkModeScreenRoot(
            onDismissRequest = onDismissRequest,
            onComplete = onComplete,
        )
    }
}
