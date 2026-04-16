package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.framework.ui.layout.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_label_jpeg
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_label_original
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_label_png
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_label_webp
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_summary_jpeg
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_summary_original
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_summary_png
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_summary_webp
import comicviewer.feature.settings.folder.generated.resources.settings_folder_thumbnail_format_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ThumbnailFormatScreen(
    currentImageFormat: ImageFormat,
    onImageFormatChange: (ImageFormat) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_thumbnail_format_title)) },
    ) {
        Column {
            ImageFormat.entries.forEach { imageFormat ->
                ListItem(
                    leadingContent = {
                        RadioButton(selected = imageFormat == currentImageFormat, onClick = null)
                    },
                    headlineContent = { Text(imageFormat.displayName) },
                    supportingContent = { Text(imageFormat.explanatoryText) },
                    modifier = Modifier
                        .clickable { onImageFormatChange(imageFormat) }
                        .padding(it.copy(top = 0.dp, bottom = 0.dp))
                        .padding(vertical = 12.dp),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

internal val ImageFormat.displayName
    @Composable
    get() = when (this) {
        ImageFormat.WEBP -> Res.string.settings_folder_thumbnail_format_label_webp
        ImageFormat.JPEG -> Res.string.settings_folder_thumbnail_format_label_jpeg
        ImageFormat.PNG -> Res.string.settings_folder_thumbnail_format_label_png
        ImageFormat.ORIGINAL -> Res.string.settings_folder_thumbnail_format_label_original
    }.let {
        stringResource(it)
    }

private val ImageFormat.explanatoryText
    @Composable
    get() = when (this) {
        ImageFormat.WEBP -> Res.string.settings_folder_thumbnail_format_summary_webp
        ImageFormat.JPEG -> Res.string.settings_folder_thumbnail_format_summary_jpeg
        ImageFormat.PNG -> Res.string.settings_folder_thumbnail_format_summary_png
        ImageFormat.ORIGINAL -> Res.string.settings_folder_thumbnail_format_summary_original
    }.let {
        stringResource(it)
    }
