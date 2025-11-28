package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val AppInfoKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AppInfoSettingsKey::class, AppInfoSettingsKey.serializer())
        subclass(LicenseKey::class, LicenseKey.serializer())
    }
}

@Serializable
data object AppInfoSettingsKey : ScreenKey

@Serializable
private data object LicenseKey : ScreenKey

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.appInfoSettingsEntryGroup(navigator: Navigator) {
    appInfoSettingsEntry(
        onBackClick = navigator::goBack,
        onLicenceClick = {
            navigator.navigate(LicenseKey)
        },
    )
    licenseEntry(onBackClick = navigator::goBack)
}

private fun EntryProviderScope<NavKey>.appInfoSettingsEntry(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
) {
    entry<AppInfoSettingsKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        AppInfoSettingsScreenRoot(
            onBackClick = onBackClick,
            onLicenceClick = onLicenceClick,
        )
    }
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.licenseEntry(onBackClick: () -> Unit) {
    entryScreen<LicenseKey, LicenseScreenContext>(
        createContext = {
            context.require<LicenseScreenContext.Factory>().createLicenseScreenContext()
        },
    ) {
        LicenseScreenRoot(onBackClick)
    }
}
