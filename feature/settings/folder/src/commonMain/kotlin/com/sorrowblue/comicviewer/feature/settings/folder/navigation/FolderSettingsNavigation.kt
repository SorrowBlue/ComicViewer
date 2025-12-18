package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.FolderThumbnailOrderScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.FolderThumbnailOrderScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFilterQualityScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFilterQualityScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFormatScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFormatScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.ImageScaleScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageScaleScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.SortTypeScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.SortTypeScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface FolderSettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(FolderSettingsNavKey.serializer()),
            toPair(SortTypeNavKey.serializer()),
            toPair(ImageScaleNavKey.serializer()),
            toPair(ImageFilterQualityNavKey.serializer()),
            toPair(ImageFormatNavKey.serializer()),
            toPair(FolderThumbnailOrderNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideFolderSettingsEntry(
        factory: FolderSettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<FolderSettingsNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings") +
                NavigationResultMetadata.resultConsumer(
                    SortTypeScreenResultKey,
                    ImageScaleScreenResultKey,
                    ImageFilterQualityScreenResultKey,
                    ImageFormatScreenResultKey,
                    FolderThumbnailOrderScreenResultKey,
                ) + NavDisplay.transitionMaterialSharedAxisZ(),
        ) {
            with(rememberRetained { factory.createFolderSettingsScreenContext() }) {
                FolderSettingsScreenRoot(
                    onBackClick = navigator::goBack,
                    onSortTypeClick = { navigator.navigate(SortTypeNavKey(it)) },
                    onFolderThumbnailOrderClick = {
                        navigator.navigate(
                            FolderThumbnailOrderNavKey(it),
                        )
                    },
                    onImageFormatClick = { navigator.navigate(ImageFormatNavKey(it)) },
                    onImageScaleClick = { navigator.navigate(ImageScaleNavKey(it)) },
                    onImageFilterQualityClick = {
                        navigator.navigate(
                            ImageFilterQualityNavKey(it),
                        )
                    },
                )
            }
        }
    }

    @Provides
    @IntoSet
    private fun provideFolderThumbnailOrderEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<FolderThumbnailOrderNavKey>(
                metadata = DialogSceneStrategy.dialog(),
            ) {
                FolderThumbnailOrderScreenRoot(
                    folderThumbnailOrder = it.folderThumbnailOrder,
                    onDismissRequest = navigator::goBack,
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideImageFilterQualityEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<ImageFilterQualityNavKey>(metadata = DialogSceneStrategy.dialog()) {
                ImageFilterQualityScreenRoot(
                    imageFilterQuality = it.imageFilterQuality,
                    onDismissRequest = navigator::goBack,
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideImageFormatEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<ImageFormatNavKey>(metadata = DialogSceneStrategy.dialog()) {
                ImageFormatScreenRoot(
                    imageFormat = it.imageFormat,
                    onDismissRequest = navigator::goBack,
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideImageScaleNavKeyEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<ImageScaleNavKey>(metadata = DialogSceneStrategy.dialog()) {
                ImageScaleScreenRoot(
                    imageScale = it.imageScale,
                    onDismissRequest = navigator::goBack,
                )
            }
        }

    @Provides
    @IntoSet
    private fun provideSortTypeEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
            entry<SortTypeNavKey>(metadata = DialogSceneStrategy.dialog()) {
                SortTypeScreenRoot(
                    sortType = it.sortType,
                    onDismissRequest = navigator::goBack,
                )
            }
        }
}
