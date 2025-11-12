package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePickerScreenRoot
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsKey
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.display.navigation.displaySettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsSerializersModule
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.folderSettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsKey
import com.sorrowblue.comicviewer.feature.settings.info.navigation.appInfoSettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenRoot
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.security.navigation.securitySettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsKey
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.viewerSettingsEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data object SettingsKey : ScreenKey

@Serializable
private data object PluginSettingsKey : ScreenKey

@Serializable
private data object ImageCacheSettingsKey : ScreenKey

@Serializable
private data object InAppLanguagePickerKey : ScreenKey

val SettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SettingsKey::class, SettingsKey.serializer())
        subclass(PluginSettingsKey::class, PluginSettingsKey.serializer())
        subclass(ImageCacheSettingsKey::class, ImageCacheSettingsKey.serializer())
        subclass(InAppLanguagePickerKey::class, InAppLanguagePickerKey.serializer())
    }
    include(DisplaySettingsKeySerializersModule)
    include(FolderSettingsSerializersModule)
    include(AppInfoKeySerializersModule)
    include(SecuritySettingsKeySerializersModule)
    include(ViewerSettingsKeySerializersModule)
}

context(graph: PlatformGraph, appNavigationState: Navigation3State)
fun EntryProviderScope<NavKey>.settingsEntryGroup(
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onTutorialClick: () -> Unit,
) {
    settingsMainEntry(
        onBackClick = {
            appNavigationState.onBackPressed()
        },
        onSettingsClick = {
            when (it) {
                SettingsItem.DISPLAY -> appNavigationState.addToBackStack(DisplaySettingsKey)
                SettingsItem.FOLDER -> appNavigationState.addToBackStack(FolderSettingsKey)
                SettingsItem.VIEWER -> appNavigationState.addToBackStack(ViewerSettingsKey)
                SettingsItem.SECURITY -> appNavigationState.addToBackStack(SecuritySettingsKey)
                SettingsItem.APP -> appNavigationState.addToBackStack(AppInfoSettingsKey)
                SettingsItem.TUTORIAL -> onTutorialClick()
                SettingsItem.Thumbnail -> appNavigationState.addToBackStack(ImageCacheSettingsKey)
                SettingsItem.Plugin -> appNavigationState.addToBackStack(PluginSettingsKey)
                SettingsItem.LANGUAGE -> appNavigationState.addToBackStack(InAppLanguagePickerKey)
            }
        },
        onSettingsLongClick = {
            when (it) {
                SettingsItem.DISPLAY,
                SettingsItem.FOLDER,
                SettingsItem.VIEWER,
                SettingsItem.SECURITY,
                SettingsItem.APP,
                SettingsItem.TUTORIAL,
                SettingsItem.Thumbnail,
                SettingsItem.Plugin,
                -> Unit

                SettingsItem.LANGUAGE -> appNavigationState.addToBackStack(InAppLanguagePickerKey)
            }
        },
    )
    pluginSettingsEntry(onBackClick = appNavigationState::onBackPressed)
    imageCacheSettingsEntry(onBackClick = appNavigationState::onBackPressed)
    inAppLanguagePickerEntry(onBackClick = appNavigationState::onBackPressed)

    displaySettingsEntryGroup()
    folderSettingsEntryGroup()
    appInfoSettingsEntryGroup()
    securitySettingsEntryGroup(
        onChangeAuthEnable = onChangeAuthEnable,
        onPasswordChangeClick = onPasswordChangeClick,
    )
    viewerSettingsEntryGroup()
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.settingsMainEntry(
    onBackClick: () -> Unit,
    onSettingsClick: (SettingsItem) -> Unit,
    onSettingsLongClick: (SettingsItem) -> Unit,
) {
    entry<SettingsKey>(
        metadata = ListDetailSceneStrategy.listPane("Settings", detailPlaceholder = {
            with(
                (graph as DisplaySettingsScreenContext.Factory).createDisplaySettingsScreenContext(),
            ) {
                DisplaySettingsScreenRoot(
                    onBackClick = onBackClick,
                    onDarkModeClick = {},
                )
            }
        }),
    ) {
        SettingsScreenRoot(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onSettingsLongClick = onSettingsLongClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.pluginSettingsEntry(onBackClick: () -> Unit) {
    entry<PluginSettingsKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        PluginScreenRoot(
            onBackClick = onBackClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.imageCacheSettingsEntry(onBackClick: () -> Unit) {
    entryScreen<ImageCacheSettingsKey, ImageCacheScreenContext>(
        createContext = {
            (graph as ImageCacheScreenContext.Factory).createImageCacheScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        ImageCacheScreenRoot(onBackClick = onBackClick)
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.inAppLanguagePickerEntry(onBackClick: () -> Unit) {
    entryScreen<InAppLanguagePickerKey, InAppLanguagePickerScreenContext>(
        createContext = {
            (graph as InAppLanguagePickerScreenContext.Factory)
                .createInAppLanguagePickerScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        InAppLanguagePickerScreenRoot(onBackClick = onBackClick)
    }
}
