package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
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

context(graph: PlatformGraph, state: Navigation3State)
fun EntryProviderScope<NavKey>.appInfoSettingsEntryGroup() {
    appInfoSettingsEntry(
        onBackClick = state::onBackPressed,
        onLicenceClick = {
            state.addToBackStack(LicenseKey)
        },
    )
    licenseEntry(onBackClick = state::onBackPressed)
}

context(graph: PlatformGraph)
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

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.licenseEntry(onBackClick: () -> Unit) {
    entryScreen<LicenseKey, LicenseScreenContext>(
        createContext = { (graph as LicenseScreenContext.Factory).createLicenseScreenContext() },
    ) {
        LicenseScreenRoot(onBackClick)
    }
}
