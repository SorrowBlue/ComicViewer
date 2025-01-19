package com.sorrowblue.comicviewer.file.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@PreviewLightDark
@Composable
private fun PreviewFileList(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        ListFile(
            file = fakeBookFile(name = "Fake book name"),
            onClick = {},
            onLongClick = {},
            showThumbnail = showThumbnail,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewFileListCard(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        ListFileCard(
            file = fakeBookFile(name = "Fake book name"),
            onClick = {},
            onLongClick = {},
            showThumbnail = showThumbnail,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None
        )
    }
}

private class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
