package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
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

context(graph: PlatformGraph, state: AppNavigationState)
fun EntryProviderScope<NavKey>.displaySettingsEntryGroup() {
    displaySettingsEntry(
        onBackClick = state::onBackPressed,
        onDarkModeClick = { state.addToBackStack(DarkModeKey) }
    )
    darkModeEntry(
        onDismissRequest = state::onBackPressed,
        onComplete = state::onBackPressed
    )
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.displaySettingsEntry(
    onBackClick: () -> Unit,
    onDarkModeClick: () -> Unit,
) {
    entryScreen<DisplaySettingsKey, DisplaySettingsScreenContext>(
        createContext = { (graph as DisplaySettingsScreenContext.Factory).createDisplaySettingsScreenContext() },
        metadata = ListDetailSceneStrategy.detailPane("Settings")
    ) {
        DisplaySettingsScreenRoot(
            onBackClick = onBackClick,
            onDarkModeClick = onDarkModeClick
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.darkModeEntry(
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<DarkModeKey, DarkModeScreenContext>(
        createContext = { (graph as DarkModeScreenContext.Factory).createDarkModeScreenContext() },
        metadata = DialogSceneStrategy.dialog()
    ) {
        DarkModeScreenRoot(
            onDismissRequest = onDismissRequest,
            onComplete = onComplete
        )
    }
}
