package com.sorrowblue.comicviewer.file.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@Preview(widthDp = 120)
@Preview(widthDp = 160)
@Preview(widthDp = 180)
@Preview(widthDp = 200)
@Composable
internal fun PreviewFileGrid() {
    PreviewTheme {
        GridFile(
            file = fakeBookFile(),
            onClick = {},
            onInfoClick = {},
            showThumbnail = true,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
        )
    }
}
