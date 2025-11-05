package com.sorrowblue.comicviewer.feature.settings.folder

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
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imageformat_label_jpeg_explanation
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imageformat_label_png_explanation
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imageformat_label_webp_explanation
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imageformat_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImageFormatScreen(
    currentImageFormat: ImageFormat,
    onImageFormatChange: (ImageFormat) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_imageformat_title)) }
    ) {
        Column {
            ImageFormat.entries.forEach { imageFormat ->
                ListItem(
                    leadingContent = {
                        RadioButton(selected = imageFormat == currentImageFormat, onClick = null)
                    },
                    headlineContent = {
                        Text(text = imageFormat.name)
                    },
                    supportingContent = {
                        Text(text = stringResource(imageFormat.explanatoryText))
                    },
                    modifier = Modifier
                        .clickable { onImageFormatChange(imageFormat) }
                        .padding(it.copy(top = 0.dp, bottom = 0.dp))
                        .padding(vertical = 12.dp),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

private val ImageFormat.explanatoryText
    get() = when (this) {
        ImageFormat.WEBP -> Res.string.settings_folder_imageformat_label_webp_explanation
        ImageFormat.JPEG -> Res.string.settings_folder_imageformat_label_jpeg_explanation
        ImageFormat.PNG -> Res.string.settings_folder_imageformat_label_png_explanation
    }
