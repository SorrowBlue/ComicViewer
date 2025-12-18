package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePickerScreenRoot
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.navigation.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface SettingsNavigation {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (SettingsNavKey::class as KClass<NavKey>) to (SettingsNavKey.serializer() as KSerializer<NavKey>),
            (ImageCacheNavKey::class as KClass<NavKey>) to (ImageCacheNavKey.serializer() as KSerializer<NavKey>),
            (InAppLanguagePickerNavKey::class as KClass<NavKey>) to (InAppLanguagePickerNavKey.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideSEntry(
        factory: DisplaySettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<SettingsNavKey>(
            metadata = ListDetailSceneStrategy.listPane(
                "Settings",
                detailPlaceholder = {
                    with(factory.createDisplaySettingsScreenContext()) {
                        DisplaySettingsScreenRoot(
                            onBackClick = navigator::goBack,
                            onDarkModeClick = {},
                        )
                    }
                },
            ) + NavDisplay.transitionMaterialFadeThrough(),
        ) {
            SettingsScreenRoot(
                onBackClick = navigator::goBack,
                onSettingsClick = {
                    when (it) {
                        SettingsItem.DISPLAY -> navigator.navigate(DisplaySettingsNavKey)
                        SettingsItem.FOLDER -> navigator.navigate(FolderSettingsNavKey)
                        SettingsItem.VIEWER -> navigator.navigate(ViewerSettingsNavKey)
                        SettingsItem.SECURITY -> navigator.navigate(SecuritySettingsNavKey)
                        SettingsItem.APP -> navigator.navigate(InfoSettingsNavKey)
                        SettingsItem.TUTORIAL -> {
                            navigator.navigate(TutorialNavKey)
                        }

                        SettingsItem.Thumbnail -> navigator.navigate(ImageCacheNavKey)
                        SettingsItem.Plugin -> navigator.navigate(PluginSettingsNavKey)
                        SettingsItem.LANGUAGE -> navigator.navigate(InAppLanguagePickerNavKey)
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

                        SettingsItem.LANGUAGE -> navigator.navigate(InAppLanguagePickerNavKey)
                    }
                }
            )
        }
    }

    @Provides
    @IntoSet
    private fun provideImageCacheEntry(
        factory: ImageCacheScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<ImageCacheNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings")
                + NavDisplay.transitionMaterialSharedAxisX()
        ) {
            with(rememberRetained { factory.createImageCacheScreenContext() }) {
                ImageCacheScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideInAppLanguagePickerEntry(
        factory: InAppLanguagePickerScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<InAppLanguagePickerNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings")
                + NavDisplay.transitionMaterialSharedAxisX()
        ) {
            with(rememberRetained { factory.createInAppLanguagePickerScreenContext() }) {
                InAppLanguagePickerScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
