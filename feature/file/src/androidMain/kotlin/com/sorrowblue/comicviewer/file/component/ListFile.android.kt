package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder

@PreviewLightDark
@Composable
private fun FileListPreview(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        Column {
            ListFile(
                file = fakeBookFile(name = "Fake book name"),
                onLongClick = {},
                showThumbnail = showThumbnail,
                fontSize = FolderDisplaySettingsDefaults.fontSize,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None
            )
            ListFile(
                file = fakeFolder(),
                onLongClick = {},
                showThumbnail = showThumbnail,
                fontSize = FolderDisplaySettingsDefaults.fontSize,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FileListCardPreview(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        Column {
            ListFileCard(
                file = fakeBookFile(name = "Fake book name"),
                onClick = {},
                onLongClick = {},
                showThumbnail = showThumbnail,
                fontSize = FolderDisplaySettingsDefaults.fontSize,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None
            )
            ListFileCard(
                file = fakeFolder(),
                onClick = {},
                onLongClick = {},
                showThumbnail = showThumbnail,
                fontSize = FolderDisplaySettingsDefaults.fontSize,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.None
            )
        }
    }
}

private class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
