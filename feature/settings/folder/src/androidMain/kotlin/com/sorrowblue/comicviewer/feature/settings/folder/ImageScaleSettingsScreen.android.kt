package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
@Preview
private fun ImageScaleSettingsScreenPreview() {
    PreviewTheme {
        var imageScale by remember { mutableStateOf(FolderDisplaySettingsDefaults.imageScale) }
        ImageScaleSettingsScreen(
            currentImageScale = imageScale,
            onImageScaleChange = { imageScale = it },
            onDismissRequest = { }
        )
    }
}
