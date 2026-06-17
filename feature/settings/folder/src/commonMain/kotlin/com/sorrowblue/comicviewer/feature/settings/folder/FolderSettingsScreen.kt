package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SliderSetting
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderThumbnailOrderNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageFilterQualityNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageFormatNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageScaleNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.SortTypeNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.filterquality.displayText
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder.displayText
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat.displayName
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailorder.displayText
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailscale.displayText
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_font_size
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_image_folder
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_save_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_files_extension
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_hidden_file
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_show_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_sort_order
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail_filter_quality
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail_format
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail_order
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail_quality
import comicviewer.feature.settings.folder.generated.resources.settings_folder_label_thumbnail_scale
import comicviewer.feature.settings.folder.generated.resources.settings_folder_title
import org.jetbrains.compose.resources.stringResource

@NavEdge(ImageFilterQualityNavKey::class)
@NavEdge(FolderThumbnailOrderNavKey::class)
@NavEdge(ImageFilterQualityNavKey::class)
@NavEdge(ImageFormatNavKey::class)
@NavEdge(ImageScaleNavKey::class)
@NavEdge(SortTypeNavKey::class)
@NavDestination(FolderSettingsNavKey::class)
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
    modifier: Modifier = Modifier,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_folder_title)) },
        onBackClick = onBackClick,
        modifier = modifier,
    ) {
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_folder_label_show_hidden_file))
            },
            checked = uiState.showHiddenFiles,
            onCheckedChange = onShowHiddenFilesChange,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_folder_label_show_files_extension))
            },
            checked = uiState.showFilesExtension,
            onCheckedChange = onShowFilesExtensionChange,
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_folder_label_sort_order))
            },
            summary = {
                Text(uiState.fileSort.displayText)
            },
            onClick = onSortTypeClick,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_folder_label_image_folder))
            },
            checked = uiState.isOpenImageFolder,
            onCheckedChange = onChangeOpenImageFolder,
        )

        SliderSetting(
            title = {
                Text(stringResource(Res.string.settings_folder_label_font_size))
            },
            thumbText = {
                Text(
                    it.toInt().toString(),
                    modifier = Modifier.widthIn(min = 40.dp),
                    textAlign = TextAlign.Center,
                )
            },
            value = uiState.fontSize.toFloat(),
            onValueChange = { onFontSizeChange(it.toInt()) },
            steps = 11,
            valueRange = 8f..20f,
        )
        SettingsCategory(title = {
            Text(stringResource(Res.string.settings_folder_label_thumbnail))
        }) {
            SwitchSetting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_show_thumbnail))
                },
                checked = uiState.showThumbnails,
                onCheckedChange = onShowThumbnailsChange,
            )
            Setting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_thumbnail_scale))
                },
                summary = {
                    Text(uiState.imageScale.displayText)
                },
                onClick = onImageScaleClick,
                enabled = uiState.showThumbnails,
            )
            Setting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_thumbnail_filter_quality))
                },
                summary = {
                    Text(stringResource(uiState.imageFilterQuality.displayText))
                },
                onClick = onImageFilterQualityClick,
                enabled = uiState.showThumbnails,
            )
            Setting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_thumbnail_format))
                },
                summary = {
                    Text(uiState.imageFormat.displayName)
                },
                onClick = onImageFormatClick,
                enabled = uiState.showThumbnails,
            )
            SliderSetting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_thumbnail_quality))
                },
                value = uiState.thumbnailQuality.toFloat(),
                enabled = uiState.showThumbnails && uiState.imageFormat != ImageFormat.ORIGINAL,
                thumbText = {
                    Text(
                        it.toInt().toString(),
                        modifier = Modifier.widthIn(min = 40.dp),
                        textAlign = TextAlign.Center,
                    )
                },
                onValueChange = { onThumbnailQualityChange(it.toInt()) },
                valueRange = 0f..100f,
            )
            SwitchSetting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_save_thumbnail))
                },
                checked = uiState.isSavedThumbnail,
                onCheckedChange = onSavedThumbnailChange,
                enabled = uiState.showThumbnails,
            )
            Setting(
                title = {
                    Text(stringResource(Res.string.settings_folder_label_thumbnail_order))
                },
                summary = {
                    Text(uiState.folderThumbnailOrder.displayText)
                },
                onClick = onFolderThumbnailOrderClick,
                enabled = uiState.showThumbnails,
            )
        }
    }
}

@NavPreview(FolderSettingsNavKey::class, primary = true)
@Preview
@Composable
private fun FolderSettingsScreenPreview() = PreviewTheme {
    FolderSettingsScreen(
        uiState = FolderSettingsScreenUiState(),
        onBackClick = {},
        onShowHiddenFilesChange = {},
        onShowFilesExtensionChange = {},
        onSortTypeClick = {},
        onShowThumbnailsChange = {},
        onImageScaleClick = {},
        onImageFilterQualityClick = {},
        onChangeOpenImageFolder = {},
        onSavedThumbnailChange = {},
        onFolderThumbnailOrderClick = {},
        onFontSizeChange = {},
        onImageFormatClick = {},
        onThumbnailQualityChange = {},
    )
}
