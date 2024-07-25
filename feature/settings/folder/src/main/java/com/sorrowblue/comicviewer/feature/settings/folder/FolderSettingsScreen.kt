package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SliderSetting
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FileSortDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderThumbnailOrderDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageFilterQualityDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageFormatDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageScaleDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.dialog.displayText
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme

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

internal interface FolderSettingsScreenNavigator : SettingsDetailNavigator {

    fun navigateToImageFormat(imageFormat: ImageFormat)
    fun navigateToFileSort(sortType: SortType)
    fun navigateToImageScale(imageScale: ImageScale)
    fun navigateToImageFilterQuality(imageFilterQuality: ImageFilterQuality)
    fun navigateToFolderThumbnailOrder(folderThumbnailOrder: FolderThumbnailOrder)
}

@Destination<FolderSettingsGraph>(
    start = true,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FolderSettingsScreen(
    contentPadding: PaddingValues,
    imageFormatResultRecipient: ResultRecipient<ImageFormatDialogDestination, ImageFormat>,
    fileSortResultRecipient: ResultRecipient<FileSortDialogDestination, SortType>,
    imageScaleResultRecipient: ResultRecipient<ImageScaleDialogDestination, ImageScale>,
    imageFilterQualityRecipient: ResultRecipient<ImageFilterQualityDialogDestination, ImageFilterQuality>,
    folderThumbnailOrderRecipient: ResultRecipient<FolderThumbnailOrderDialogDestination, FolderThumbnailOrder>,
    navigator: FolderSettingsScreenNavigator,
) {
    FolderSettingsScreen(
        contentPadding = contentPadding,
        onBackClick = navigator::navigateBack,
        onImageFormatClick = navigator::navigateToImageFormat,
        onFileSortClick = navigator::navigateToFileSort,
        onImageScaleClick = navigator::navigateToImageScale,
        onImageFilterQualityClick = navigator::navigateToImageFilterQuality,
        onFolderThumbnailOrderClick = navigator::navigateToFolderThumbnailOrder,
        imageFormatResultRecipient = imageFormatResultRecipient,
        fileSortResultRecipient = fileSortResultRecipient,
        imageScaleResultRecipient = imageScaleResultRecipient,
        imageFilterQualityRecipient = imageFilterQualityRecipient,
        folderThumbnailOrderRecipient = folderThumbnailOrderRecipient
    )
}

@Composable
private fun FolderSettingsScreen(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onImageFormatClick: (ImageFormat) -> Unit,
    onFileSortClick: (SortType) -> Unit,
    onImageScaleClick: (ImageScale) -> Unit,
    onImageFilterQualityClick: (ImageFilterQuality) -> Unit,
    onFolderThumbnailOrderClick: (FolderThumbnailOrder) -> Unit,
    imageFormatResultRecipient: ResultRecipient<ImageFormatDialogDestination, ImageFormat>,
    fileSortResultRecipient: ResultRecipient<FileSortDialogDestination, SortType>,
    imageScaleResultRecipient: ResultRecipient<ImageScaleDialogDestination, ImageScale>,
    imageFilterQualityRecipient: ResultRecipient<ImageFilterQualityDialogDestination, ImageFilterQuality>,
    folderThumbnailOrderRecipient: ResultRecipient<FolderThumbnailOrderDialogDestination, FolderThumbnailOrder>,
    state: FolderSettingsScreenState = rememberFolderSettingsScreenState(),
) {
    FolderSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onShowHiddenFilesChange = state::onShowHiddenFilesChange,
        onShowThumbnailsChange = state::onShowThumbnailsChange,
        onFileSortClick = { onFileSortClick(state.uiState.fileSort) },
        onImageScaleClick = { onImageScaleClick(state.uiState.imageScale) },
        onImageFilterQualityClick = { onImageFilterQualityClick(state.uiState.imageFilterQuality) },
        onShowFilesExtensionChange = state::onShowFilesExtensionChange,
        onChangeOpenImageFolder = state::onChangeOpenImageFolder,
        onSavedThumbnailChange = state::onSavedThumbnailChange,
        onFontSizeChange = state::onFontSizeChange,
        onImageFormatClick = { onImageFormatClick(state.uiState.imageFormat) },
        onThumbnailQualityChange = state::onThumbnailQualityChange,
        onFolderThumbnailOrderClick = { onFolderThumbnailOrderClick(state.uiState.folderThumbnailOrder) },
        contentPadding = contentPadding
    )

    imageFormatResultRecipient.onNavResult(state::onImageFormatChange)
    fileSortResultRecipient.onNavResult(state::onFileSortChange)
    imageScaleResultRecipient.onNavResult(state::onImageScaleChange)
    imageFilterQualityRecipient.onNavResult(state::onImageFilterChange)
    folderThumbnailOrderRecipient.onNavResult(state::onFolderThumbnailOrder)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderSettingsScreen(
    uiState: FolderSettingsScreenUiState,
    onBackClick: () -> Unit,
    onShowHiddenFilesChange: (Boolean) -> Unit,
    onShowFilesExtensionChange: (Boolean) -> Unit,
    onFileSortClick: () -> Unit,
    onShowThumbnailsChange: (Boolean) -> Unit,
    onImageScaleClick: () -> Unit,
    onImageFilterQualityClick: () -> Unit,
    onChangeOpenImageFolder: (Boolean) -> Unit,
    onSavedThumbnailChange: (Boolean) -> Unit,
    onFolderThumbnailOrderClick: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onImageFormatClick: () -> Unit,
    onThumbnailQualityChange: (Int) -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsDetailPane(
        title = {
            Text(text = stringResource(id = R.string.settings_folder_title))
        },
        onBackClick = onBackClick,
        contentPadding = contentPadding
    ) {
        SwitchSetting(
            title = R.string.settings_folder_label_show_hidden_files,
            checked = uiState.showHiddenFiles,
            onCheckedChange = onShowHiddenFilesChange
        )
        SwitchSetting(
            title = R.string.settings_folder_label_show_files_extension,
            checked = uiState.showFilesExtension,
            onCheckedChange = onShowFilesExtensionChange
        )
        Setting(
            title = R.string.settings_folder_label_file_sort,
            summary = uiState.fileSort.displayText,
            onClick = onFileSortClick
        )
        SwitchSetting(
            title = R.string.settings_folder_label_image_folder,
            summary = R.string.settings_folder_desc_image_folder,
            checked = uiState.isOpenImageFolder,
            onCheckedChange = onChangeOpenImageFolder
        )

        SliderSetting(
            title = R.string.settings_folder_label_font_size,
            value = uiState.fontSize,
            onValueChange = onFontSizeChange,
            valueRange = 8..20
        )
        SettingsCategory(title = R.string.settings_folder_label_thumbnail) {
            SwitchSetting(
                title = R.string.settings_folder_label_show_thumbnail,
                checked = uiState.showThumbnails,
                onCheckedChange = onShowThumbnailsChange
            )
            Setting(
                title = R.string.settings_folder_label_image_scale,
                summary = uiState.imageScale.displayText,
                onClick = onImageScaleClick
            )
            Setting(
                title = R.string.settings_folder_label_image_filter_quality,
                summary = uiState.imageFilterQuality.displayText,
                onClick = onImageFilterQualityClick
            )
            Setting(
                title = R.string.settings_folder_label_image_format,
                summary = uiState.imageFormat.summaryText,
                onClick = onImageFormatClick
            )
            SliderSetting(
                title = R.string.settings_folder_label_image_quality,
                value = uiState.thumbnailQuality,
                onValueChange = onThumbnailQualityChange,
                valueRange = 0..100
            )
            SwitchSetting(
                title = R.string.settings_folder_label_save_thumbnail,
                checked = uiState.isSavedThumbnail,
                onCheckedChange = onSavedThumbnailChange
            )
            Setting(
                title = R.string.settings_folder_label_folder_thumbnail_order,
                summary = uiState.folderThumbnailOrder.displayText,
                onClick = onFolderThumbnailOrderClick
            )
        }
    }
}

private val ImageFormat.summaryText
    get() = when (this) {
        ImageFormat.WEBP -> R.string.settings_folder_label_webp_summary
        ImageFormat.JPEG -> R.string.settings_folder_label_jpeg_summary
        ImageFormat.PNG -> R.string.settings_folder_label_png_summary
    }

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
            onFolderThumbnailOrderClick = {},
            contentPadding = PaddingValues()
        )
    }
}
