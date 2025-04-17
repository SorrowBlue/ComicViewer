package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Preview
@Composable
private fun FolderSettingsScreenPreview() {
    PreviewTheme {
        FolderSettingsScreen(
            uiState = FolderSettingsScreenUiState(),
            onBackClick = {},
            onShowHiddenFilesChange = {},
            onShowFilesExtensionChange = {},
            onShowThumbnailsChange = {},
            onFileSortClick = {},
            onImageScaleClick = {},
            onImageFilterQualityClick = {},
            onChangeOpenImageFolder = {},
            onSavedThumbnailChange = {},
            onFontSizeChange = {},
            onImageFormatClick = {},
            onThumbnailQualityChange = {},
            onFolderThumbnailOrderClick = {}
        )
    }
}
