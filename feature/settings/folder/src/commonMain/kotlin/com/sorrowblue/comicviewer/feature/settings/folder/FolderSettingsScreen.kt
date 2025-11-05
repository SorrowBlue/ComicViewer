package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SliderSetting
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_desc_image_folder
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_file_sort
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_folder_thumbnail_order
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_font_size
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_filter_quality
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_folder
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_format
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_quality
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_scale
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_jpeg_summary
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_png_summary
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_save_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_files_extension
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_hidden_files
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_webp_summary
import comicviewer.feature.settings.folder.generated.resources.settings_folder_title
import org.jetbrains.compose.resources.stringResource

internal data class FolderSettingsScreenUiState(
    val showHiddenFiles: Boolean = FolderDisplaySettingsDefaults.isDisplayHiddenFile,
    val showFilesExtension: Boolean = FolderDisplaySettingsDefaults.isDisplayFileExtension,
    val fileSort: SortType = FolderDisplaySettingsDefaults.sortType,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.isDisplayThumbnail,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
    val imageFormat: ImageFormat = FolderDisplaySettingsDefaults.imageFormat,
    val thumbnailQuality: Int = FolderDisplaySettingsDefaults.thumbnailQuality,
    val isSavedThumbnail: Boolean = FolderDisplaySettingsDefaults.isSavedThumbnail,
    val isOpenImageFolder: Boolean = false,
    val fontSize: Int = FolderDisplaySettingsDefaults.fontSize,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderDisplaySettingsDefaults.folderThumbnailOrder,
)

@Composable
internal fun FolderSettingsScreen(
    uiState: FolderSettingsScreenUiState,
    onBackClick: () -> Unit,
    onShowHiddenFilesChange: (Boolean) -> Unit,
    onShowFilesExtensionChange: (Boolean) -> Unit,
    onSortTypeClick: () -> Unit,
    onShowThumbnailsChange: (Boolean) -> Unit,
    onImageScaleClick: () -> Unit,
    onImageFilterQualityClick: () -> Unit,
    onChangeOpenImageFolder: (Boolean) -> Unit,
    onSavedThumbnailChange: (Boolean) -> Unit,
    onFolderThumbnailOrderClick: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onImageFormatClick: () -> Unit,
    onThumbnailQualityChange: (Int) -> Unit,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_folder_title)) },
        onBackClick = onBackClick,
    ) {
        SwitchSetting(
            title = Res.string.settings_folder_label_show_hidden_files,
            checked = uiState.showHiddenFiles,
            onCheckedChange = onShowHiddenFilesChange
        )
        SwitchSetting(
            title = Res.string.settings_folder_label_show_files_extension,
            checked = uiState.showFilesExtension,
            onCheckedChange = onShowFilesExtensionChange
        )
        Setting(
            title = Res.string.settings_folder_label_file_sort,
            summary = uiState.fileSort.displayText,
            onClick = onSortTypeClick
        )
        SwitchSetting(
            title = Res.string.settings_folder_label_image_folder,
            summary = Res.string.settings_folder_desc_image_folder,
            checked = uiState.isOpenImageFolder,
            onCheckedChange = onChangeOpenImageFolder
        )

        SliderSetting(
            title = Res.string.settings_folder_label_font_size,
            value = uiState.fontSize,
            onValueChange = onFontSizeChange,
            valueRange = 8..20
        )
        SettingsCategory(title = Res.string.settings_folder_label_thumbnail) {
            SwitchSetting(
                title = Res.string.settings_folder_label_show_thumbnail,
                checked = uiState.showThumbnails,
                onCheckedChange = onShowThumbnailsChange
            )
            Setting(
                title = Res.string.settings_folder_label_image_scale,
                summary = uiState.imageScale.displayText,
                onClick = onImageScaleClick
            )
            Setting(
                title = Res.string.settings_folder_label_image_filter_quality,
                summary = uiState.imageFilterQuality.displayText,
                onClick = onImageFilterQualityClick
            )
            Setting(
                title = Res.string.settings_folder_label_image_format,
                summary = uiState.imageFormat.summaryText,
                onClick = onImageFormatClick
            )
            SliderSetting(
                title = Res.string.settings_folder_label_image_quality,
                value = uiState.thumbnailQuality,
                onValueChange = onThumbnailQualityChange,
                valueRange = 0..100
            )
            SwitchSetting(
                title = Res.string.settings_folder_label_save_thumbnail,
                checked = uiState.isSavedThumbnail,
                onCheckedChange = onSavedThumbnailChange
            )
            Setting(
                title = Res.string.settings_folder_label_folder_thumbnail_order,
                summary = uiState.folderThumbnailOrder.displayText,
                onClick = onFolderThumbnailOrderClick
            )
        }
    }
}

private val ImageFormat.summaryText
    get() = when (this) {
        ImageFormat.WEBP -> Res.string.settings_folder_label_webp_summary
        ImageFormat.JPEG -> Res.string.settings_folder_label_jpeg_summary
        ImageFormat.PNG -> Res.string.settings_folder_label_png_summary
    }
