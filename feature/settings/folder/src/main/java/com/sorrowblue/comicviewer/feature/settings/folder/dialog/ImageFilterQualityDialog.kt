package com.sorrowblue.comicviewer.feature.settings.folder.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.feature.settings.folder.R
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Destination<FolderSettingsGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ImageFilterQualityDialog(
    imageFilterQuality: ImageFilterQuality,
    resultNavigator: ResultBackNavigator<ImageFilterQuality>,
) {
    ImageFilterQualityDialog(
        currentImageFilterQuality = imageFilterQuality,
        onImageFilterQualityChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
private fun ImageFilterQualityDialog(
    currentImageFilterQuality: ImageFilterQuality,
    onImageFilterQualityChange: (ImageFilterQuality) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.settings_folder_imagefilterquality_title)) }
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
        ImageFilterQuality.None -> R.string.settings_folder_imagefilterquality_label_none
        ImageFilterQuality.Low -> R.string.settings_folder_imagefilterquality_label_low
        ImageFilterQuality.Medium -> R.string.settings_folder_imagefilterquality_label_medium
        ImageFilterQuality.High -> R.string.settings_folder_imagefilterquality_label_high
    }

@Composable
@Preview
private fun ImageFilterDialogPreview() {
    PreviewTheme {
        var imageFilterQuality by remember { mutableStateOf(FolderDisplaySettingsDefaults.imageFilterQuality) }
        ImageFilterQualityDialog(
            currentImageFilterQuality = imageFilterQuality,
            onImageFilterQualityChange = { imageFilterQuality = it },
            onDismissRequest = { }
        )
    }
}
