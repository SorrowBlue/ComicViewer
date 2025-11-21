package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
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
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.PluginSettingsKey
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.PluginSettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.pluginSettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.security.navigation.securitySettingsEntryGroup
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsKey
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.viewerSettingsEntryGroup
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data object SettingsKey : ScreenKey

@Serializable
private data object ImageCacheSettingsKey : ScreenKey

@Serializable
private data object InAppLanguagePickerKey : ScreenKey

val SettingsKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SettingsKey::class, SettingsKey.serializer())
        subclass(ImageCacheSettingsKey::class, ImageCacheSettingsKey.serializer())
        subclass(InAppLanguagePickerKey::class, InAppLanguagePickerKey.serializer())
    }
    include(DisplaySettingsKeySerializersModule)
    include(FolderSettingsSerializersModule)
    include(AppInfoKeySerializersModule)
    include(SecuritySettingsKeySerializersModule)
    include(ViewerSettingsKeySerializersModule)
    include(PluginSettingsKeySerializersModule)
}

context(graph: PlatformGraph)
fun EntryProviderScope<NavKey>.settingsEntryGroup(
    navigator: Navigator,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onTutorialClick: () -> Unit,
) {
    settingsMainEntry(
        onBackClick = navigator::goBack,
        onSettingsClick = {
            when (it) {
                SettingsItem.DISPLAY -> navigator.navigate(DisplaySettingsKey)
                SettingsItem.FOLDER -> navigator.navigate(FolderSettingsKey)
                SettingsItem.VIEWER -> navigator.navigate(ViewerSettingsKey)
                SettingsItem.SECURITY -> navigator.navigate(SecuritySettingsKey)
                SettingsItem.APP -> navigator.navigate(AppInfoSettingsKey)
                SettingsItem.TUTORIAL -> onTutorialClick()
                SettingsItem.Thumbnail -> navigator.navigate(ImageCacheSettingsKey)
                SettingsItem.Plugin -> navigator.navigate(PluginSettingsKey)
                SettingsItem.LANGUAGE -> navigator.navigate(InAppLanguagePickerKey)
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

                SettingsItem.LANGUAGE -> navigator.navigate(InAppLanguagePickerKey)
            }
        },
    )
    imageCacheSettingsEntry(onBackClick = navigator::goBack)
    inAppLanguagePickerEntry(onBackClick = navigator::goBack)

    displaySettingsEntryGroup(navigator = navigator)
    folderSettingsEntryGroup(navigator = navigator)
    appInfoSettingsEntryGroup(navigator = navigator)
    securitySettingsEntryGroup(
        navigator = navigator,
        onChangeAuthEnable = onChangeAuthEnable,
        onPasswordChangeClick = onPasswordChangeClick,
    )
    viewerSettingsEntryGroup(navigator = navigator)
    pluginSettingsEntryGroup(navigator = navigator)
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
