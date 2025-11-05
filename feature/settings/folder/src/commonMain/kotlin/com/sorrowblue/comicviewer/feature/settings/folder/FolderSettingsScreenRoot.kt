package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.ui.NavigationResultEffect
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultConsumer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.getResultState
import kotlinx.serialization.json.Json

@Composable
context(context: FolderSettingsScreenContext)
fun FolderSettingsScreenRoot(
    onBackClick: () -> Unit,
    onSortTypeClick: (SortType) -> Unit,
    onImageScaleClick: (ImageScale) -> Unit,
    onImageFilterQualityClick: (ImageFilterQuality) -> Unit,
    onImageFormatClick: (ImageFormat) -> Unit,
    onFolderThumbnailOrderClick: (FolderThumbnailOrder) -> Unit,
) {
    val state = rememberFolderSettingsScreenState()
    FolderSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onShowHiddenFilesChange = state::onShowHiddenFilesChange,
        onShowThumbnailsChange = state::onShowThumbnailsChange,
        onSortTypeClick = { onSortTypeClick(state.uiState.fileSort) },
        onImageScaleClick = { onImageScaleClick(state.uiState.imageScale) },
        onImageFilterQualityClick = { onImageFilterQualityClick(state.uiState.imageFilterQuality) },
        onShowFilesExtensionChange = state::onShowFilesExtensionChange,
        onChangeOpenImageFolder = state::onChangeOpenImageFolder,
        onSavedThumbnailChange = state::onSavedThumbnailChange,
        onFontSizeChange = state::onFontSizeChange,
        onImageFormatClick = { onImageFormatClick(state.uiState.imageFormat) },
        onThumbnailQualityChange = state::onThumbnailQualityChange,
        onFolderThumbnailOrderClick = { onFolderThumbnailOrderClick(state.uiState.folderThumbnailOrder) },
    )

    NavigationResultEffect(SortTypeScreenResultKey, state::onFileSortChange)
    NavigationResultEffect(ImageScaleScreenResultKey, state::onImageScaleChange)
    NavigationResultEffect(ImageFilterQualityScreenResultKey, state::onImageFilterQualityChange)
    NavigationResultEffect(ImageFormatScreenResultKey, state::onImageFormatChange)
    NavigationResultEffect(FolderThumbnailOrderScreenResultKey, state::onFolderThumbnailOrderChange)
}

val SortTypeScreenResultKey = SerializableNavigationResultKey(
    serializer = SortType.serializer(),
    resultKey = "SortTypeScreenResultKey",
)

val ImageScaleScreenResultKey = SerializableNavigationResultKey(
    serializer = ImageScale.serializer(),
    resultKey = "ImageScaleScreenResultKey",
)

val ImageFilterQualityScreenResultKey: SerializableNavigationResultKey<ImageFilterQuality> =
    SerializableNavigationResultKey(
        serializer = ImageFilterQuality.serializer(),
        resultKey = "ImageFilterQualityScreenResultKey",
    )

val ImageFormatScreenResultKey = SerializableNavigationResultKey(
    serializer = ImageFormat.serializer(),
    resultKey = "ImageFormatScreenResultKey",
)

val FolderThumbnailOrderScreenResultKey = SerializableNavigationResultKey(
    serializer = FolderThumbnailOrder.serializer(),
    resultKey = "FolderThumbnailOrderScreenResultKey",
)
