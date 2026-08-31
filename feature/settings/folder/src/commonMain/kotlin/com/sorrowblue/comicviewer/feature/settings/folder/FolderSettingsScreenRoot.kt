/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.result.ResultEffect
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType

@Composable
internal fun FolderSettingsScreenRoot(
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
        onShowFilesExtensionChange = state::onShowFilesExtensionChange,
        onSortTypeClick = { onSortTypeClick(state.uiState.fileSort) },
        onShowThumbnailsChange = state::onShowThumbnailsChange,
        onImageScaleClick = { onImageScaleClick(state.uiState.imageScale) },
        onImageFilterQualityClick = { onImageFilterQualityClick(state.uiState.imageFilterQuality) },
        onChangeOpenImageFolder = state::onChangeOpenImageFolder,
        onSavedThumbnailChange = state::onSavedThumbnailChange,
        onFolderThumbnailOrderClick = {
            onFolderThumbnailOrderClick(
                state.uiState.folderThumbnailOrder,
            )
        },
        onFontSizeChange = state::onFontSizeChange,
        onImageFormatClick = { onImageFormatClick(state.uiState.imageFormat) },
        onThumbnailQualityChange = state::onThumbnailQualityChange,
        modifier = Modifier.testTag("FolderSettingsRoot"),
    )

    ResultEffect(onResult = state::onFileSortChange)
    ResultEffect(onResult = state::onImageScaleChange)
    ResultEffect(onResult = state::onImageFilterQualityChange)
    ResultEffect(onResult = state::onImageFormatChange)
    ResultEffect(onResult = state::onFolderThumbnailOrderChange)
}
