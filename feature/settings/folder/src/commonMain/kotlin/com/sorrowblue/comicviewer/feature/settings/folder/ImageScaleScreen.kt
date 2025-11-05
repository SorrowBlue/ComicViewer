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
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagescale_label_crop
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagescale_label_fit
import comicviewer.feature.settings.folder.generated.resources.settings_folder_imagescale_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImageScaleScreen(
    currentImageScale: ImageScale,
    onImageScaleChange: (ImageScale) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.settings_folder_imagescale_title)) }
    ) {
        Column {
            ImageScale.entries.forEach { imageScale ->
                ListItem(
                    modifier = Modifier
                        .clickable { onImageScaleChange(imageScale) },
                    leadingContent = {
                        RadioButton(selected = currentImageScale == imageScale, onClick = null)
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
        ImageScale.Crop -> Res.string.settings_folder_imagescale_label_crop
        ImageScale.Fit -> Res.string.settings_folder_imagescale_label_fit
    }
