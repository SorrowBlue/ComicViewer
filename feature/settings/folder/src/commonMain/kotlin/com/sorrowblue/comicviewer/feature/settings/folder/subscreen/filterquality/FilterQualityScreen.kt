package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.filterquality

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.ImageFilterQualityNavKey
import com.sorrowblue.comicviewer.framework.ui.material3.AlertDialog
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.folder.generated.resources.Res
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filterquality_label_high
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filterquality_label_low
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filterquality_label_medium
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filterquality_label_none
import comicviewer.feature.settings.folder.generated.resources.settings_folder_filterquality_title
import org.jetbrains.compose.resources.stringResource

@NavDestination(ImageFilterQualityNavKey::class)
@Composable
internal fun FilterQualityScreen(
    currentImageFilterQuality: ImageFilterQuality,
    onImageFilterQualityChange: (ImageFilterQuality) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(Res.string.settings_folder_filterquality_title),
            )
        },
    ) {
        Column {
            ImageFilterQuality.entries.forEach { imageFilterQuality ->
                ListItem(
                    modifier = Modifier
                        .clickable { onImageFilterQualityChange(imageFilterQuality) },
                    leadingContent = {
                        RadioButton(
                            selected = imageFilterQuality == currentImageFilterQuality,
                            onClick = null,
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(imageFilterQuality.displayText))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

internal val ImageFilterQuality.displayText
    get() = when (this) {
        ImageFilterQuality.None -> Res.string.settings_folder_filterquality_label_none
        ImageFilterQuality.Low -> Res.string.settings_folder_filterquality_label_low
        ImageFilterQuality.Medium -> Res.string.settings_folder_filterquality_label_medium
        ImageFilterQuality.High -> Res.string.settings_folder_filterquality_label_high
    }

@NavPreview(ImageFilterQualityNavKey::class, primary = true)
@Preview
@Composable
private fun FilterQualityScreenPreview() = PreviewTheme {
    FilterQualityScreen(
        currentImageFilterQuality = ImageFilterQuality.Medium,
        onImageFilterQualityChange = {},
        onDismissRequest = {},
    )
}
