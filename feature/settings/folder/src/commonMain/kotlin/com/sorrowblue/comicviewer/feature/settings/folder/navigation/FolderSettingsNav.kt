package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
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
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val FolderSettingsSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(FolderSettingsKey::class, FolderSettingsKey.serializer())
        subclass(FolderThumbnailOrderKey::class, FolderThumbnailOrderKey.serializer())
        subclass(ImageFilterQualityKey::class, ImageFilterQualityKey.serializer())
        subclass(ImageFormatKey::class, ImageFormatKey.serializer())
        subclass(ImageScaleKey::class, ImageScaleKey.serializer())
        subclass(SortTypeKey::class, SortTypeKey.serializer())
    }
}

@Serializable
data object FolderSettingsKey : ScreenKey

@Serializable
private data class FolderThumbnailOrderKey(val folderThumbnailOrder: FolderThumbnailOrder) :
    ScreenKey

@Serializable
private data class ImageFilterQualityKey(val imageFilterQuality: ImageFilterQuality) : ScreenKey

@Serializable
private data class ImageFormatKey(val imageFormat: ImageFormat) : ScreenKey

@Serializable
private data class ImageScaleKey(val imageScale: ImageScale) : ScreenKey

@Serializable
private data class SortTypeKey(val sortType: SortType) : ScreenKey

context(graph: PlatformGraph, state: Navigation3State)
fun EntryProviderScope<NavKey>.folderSettingsEntryGroup() {
    folderSettingsEntry(
        onBackClick = state::onBackPressed,
        onSortTypeClick = {
            state.addToBackStack(SortTypeKey(it))
        },
        onFolderThumbnailOrderClick = {
            state.addToBackStack(FolderThumbnailOrderKey(it))
        },
        onImageFormatClick = {
            state.addToBackStack(ImageFormatKey(it))
        },
        onImageScaleClick = {
            state.addToBackStack(ImageScaleKey(it))
        },
        onImageFilterQualityClick = {
            state.addToBackStack(ImageFilterQualityKey(it))
        },
    )

    folderThumbnailOrderEntry(onDismissRequest = state::onBackPressed)
    imageFilterQualityEntry(onDismissRequest = state::onBackPressed)
    imageFormatEntry(onDismissRequest = state::onBackPressed)
    imageScaleEntry(onDismissRequest = state::onBackPressed)
    sortTypeEntry(onDismissRequest = state::onBackPressed)
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.folderSettingsEntry(
    onBackClick: () -> Unit,
    onSortTypeClick: (SortType) -> Unit,
    onFolderThumbnailOrderClick: (FolderThumbnailOrder) -> Unit,
    onImageFormatClick: (ImageFormat) -> Unit,
    onImageScaleClick: (ImageScale) -> Unit,
    onImageFilterQualityClick: (ImageFilterQuality) -> Unit,
) {
    entryScreen<FolderSettingsKey, FolderSettingsScreenContext>(
        createContext = {
            (graph as FolderSettingsScreenContext.Factory)
                .createFolderSettingsScreenContext()
        },
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavigationResultMetadata.resultConsumer(
                SortTypeScreenResultKey,
                ImageScaleScreenResultKey,
                ImageFilterQualityScreenResultKey,
                ImageFormatScreenResultKey,
                FolderThumbnailOrderScreenResultKey,
            ),
    ) {
        FolderSettingsScreenRoot(
            onBackClick = onBackClick,
            onSortTypeClick = onSortTypeClick,
            onFolderThumbnailOrderClick = onFolderThumbnailOrderClick,
            onImageFormatClick = onImageFormatClick,
            onImageScaleClick = onImageScaleClick,
            onImageFilterQualityClick = onImageFilterQualityClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.folderThumbnailOrderEntry(onDismissRequest: () -> Unit) {
    entry<FolderThumbnailOrderKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        FolderThumbnailOrderScreenRoot(
            folderThumbnailOrder = it.folderThumbnailOrder,
            onDismissRequest = onDismissRequest,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.imageFilterQualityEntry(onDismissRequest: () -> Unit) {
    entry<ImageFilterQualityKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageFilterQualityScreenRoot(
            imageFilterQuality = it.imageFilterQuality,
            onDismissRequest = onDismissRequest,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.imageFormatEntry(onDismissRequest: () -> Unit) {
    entry<ImageFormatKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageFormatScreenRoot(
            imageFormat = it.imageFormat,
            onDismissRequest = onDismissRequest,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.imageScaleEntry(onDismissRequest: () -> Unit) {
    entry<ImageScaleKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageScaleScreenRoot(
            imageScale = it.imageScale,
            onDismissRequest = onDismissRequest,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.sortTypeEntry(onDismissRequest: () -> Unit) {
    entry<SortTypeKey>(metadata = DialogSceneStrategy.dialog()) {
        SortTypeScreenRoot(
            sortType = it.sortType,
            onDismissRequest = onDismissRequest,
        )
    }
}
