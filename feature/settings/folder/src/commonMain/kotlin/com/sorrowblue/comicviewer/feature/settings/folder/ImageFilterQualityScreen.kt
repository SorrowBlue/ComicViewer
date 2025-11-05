package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagefilterquality_label_high
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagefilterquality_label_low
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagefilterquality_label_medium
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagefilterquality_label_none
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagefilterquality_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImageFilterQualityScreen(
    currentImageFilterQuality: ImageFilterQuality,
    onImageFilterQualityChange: (ImageFilterQuality) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_imagefilterquality_title)) }
    ) {
        Column {
            ImageFilterQuality.entries.forEach { imageFilterQuality ->
                ListItem(
                    modifier = Modifier
                        .clickable { onImageFilterQualityChange(imageFilterQuality) },
                    leadingContent = {
                        RadioButton(
                            selected = imageFilterQuality == currentImageFilterQuality,
                            onClick = null
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(imageFilterQuality.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

internal val ImageFilterQuality.displayText
    get() = when (this) {
        ImageFilterQuality.None -> Res.string.settings_folder_imagefilterquality_label_none
        ImageFilterQuality.Low -> Res.string.settings_folder_imagefilterquality_label_low
        ImageFilterQuality.Medium -> Res.string.settings_folder_imagefilterquality_label_medium
        ImageFilterQuality.High -> Res.string.settings_folder_imagefilterquality_label_high
    }
