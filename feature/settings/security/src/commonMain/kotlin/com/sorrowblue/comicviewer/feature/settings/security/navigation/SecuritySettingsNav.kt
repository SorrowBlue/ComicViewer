package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
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

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.securitySettingsEntryGroup(
    navigator: Navigator,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    securitySettingsEntry(
        onBackClick = navigator::goBack,
        onChangeAuthEnable = onChangeAuthEnable,
        onPasswordChangeClick = onPasswordChangeClick,
    )
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.securitySettingsEntry(
    onBackClick: () -> Unit,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    entryScreen<SecuritySettingsKey, SecuritySettingsScreenContext>(
        createContext = {
            context.require<SecuritySettingsScreenContext.Factory>()
                .createSecuritySettingsScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane(
            "Settings",
        ) + NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        SecuritySettingsScreenRoot(
            onBackClick = onBackClick,
            onChangeAuthEnable = onChangeAuthEnable,
            onPasswordChangeClick = onPasswordChangeClick,
        )
    }
}
