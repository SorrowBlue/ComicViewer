package com.sorrowblue.comicviewer.feature.settings.folder.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.feature.settings.folder.R
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Destination<FolderSettingsGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ImageFormatDialog(
    currentImageFormat: ImageFormat,
    resultNavigator: ResultBackNavigator<ImageFormat>,
) {
    ImageFormatDialog(
        currentImageFormat = currentImageFormat,
        onImageFormatChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
private fun ImageFormatDialog(
    currentImageFormat: ImageFormat,
    onImageFormatChange: (ImageFormat) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.settings_folder_imageformat_title)) }
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

private val ImageFormat.explanatoryText: Int
    get() = when (this) {
        ImageFormat.WEBP -> R.string.settings_folder_imageformat_label_webp_explanation
        ImageFormat.JPEG -> R.string.settings_folder_imageformat_label_jpeg_explanation
        ImageFormat.PNG -> R.string.settings_folder_imageformat_label_png_explanation
    }

@Composable
@Preview
private fun ImageFormatDialogPreview() {
    PreviewTheme {
        var imageFormat by remember { mutableStateOf(FolderDisplaySettingsDefaults.imageFormat) }
        ImageFormatDialog(
            currentImageFormat = imageFormat,
            onImageFormatChange = { imageFormat = it },
            onDismissRequest = {}
        )
    }
}
