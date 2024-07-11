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
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.feature.settings.folder.R
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Destination<FolderSettingsGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ImageScaleDialog(
    imageScale: ImageScale,
    resultNavigator: ResultBackNavigator<ImageScale>,
) {
    ImageScaleDialog(
        currentImageScale = imageScale,
        onImageScaleChange = resultNavigator::navigateBack,
        onDismissRequest = resultNavigator::navigateBack
    )
}

@Composable
private fun ImageScaleDialog(
    currentImageScale: ImageScale,
    onImageScaleChange: (ImageScale) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.settings_folder_imagescale_title)) }
    ) {
        Column {
            ImageScale.entries.forEach { imageScale ->
                ListItem(
                    modifier = Modifier
                        .clickable { onImageScaleChange(imageScale) },
                    leadingContent = {
                        RadioButton(selected = imageScale == currentImageScale, onClick = null)
                    },
                    headlineContent = {
                        Text(text = stringResource(imageScale.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

internal val ImageScale.displayText
    get() = when (this) {
        ImageScale.Crop -> R.string.settings_folder_imagescale_label_crop
        ImageScale.Fit -> R.string.settings_folder_imagescale_label_fit
    }

@Composable
@Preview
private fun ImageScaleDialogPreview() {
    PreviewTheme {
        var imageScale by remember { mutableStateOf(FolderDisplaySettingsDefaults.imageScale) }
        ImageScaleDialog(
            currentImageScale = imageScale,
            onImageScaleChange = { imageScale = it },
            onDismissRequest = { }
        )
    }
}
