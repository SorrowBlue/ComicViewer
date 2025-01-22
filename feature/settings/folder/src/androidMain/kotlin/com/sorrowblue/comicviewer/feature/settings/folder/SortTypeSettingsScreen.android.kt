package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
@Preview
private fun SortTypeSettingsScreenPreview() {
    PreviewTheme {
        var imageFormat by remember { mutableStateOf<SortType>(SortType.Name(true)) }
        SortTypeSettingsScreen(
            currentFileSort = imageFormat,
            onFileSortChange = { imageFormat = it },
            onDismissRequest = {}
        )
    }
}
