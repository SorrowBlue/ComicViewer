package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val SecuritySettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SecuritySettingsKey::class, SecuritySettingsKey.serializer())
    }
}

@Serializable
data object SecuritySettingsKey : ScreenKey

context(graph: PlatformGraph, state: Navigation3State)
fun EntryProviderScope<NavKey>.securitySettingsEntryGroup(
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    securitySettingsEntry(
        onBackClick = state::onBackPressed,
        onChangeAuthEnable = onChangeAuthEnable,
        onPasswordChangeClick = onPasswordChangeClick,
    )
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.securitySettingsEntry(
    onBackClick: () -> Unit,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    entryScreen<SecuritySettingsKey, SecuritySettingsScreenContext>(
        createContext = {
            (graph as SecuritySettingsScreenContext.Factory)
                .createSecuritySettingsScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        SecuritySettingsScreenRoot(
            onBackClick = onBackClick,
            onChangeAuthEnable = onChangeAuthEnable,
            onPasswordChangeClick = onPasswordChangeClick,
        )
    }
}
