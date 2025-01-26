package com.sorrowblue.comicviewer.feature.bookshelf.component

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer

@Preview(widthDp = 360, apiLevel = 35, device = Devices.PIXEL_7)
@Preview(widthDp = 400, apiLevel = 35, device = Devices.PIXEL_7)
@Preview(
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    apiLevel = 35,
    device = Devices.PIXEL_7
)
@Preview(
    widthDp = 400,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    apiLevel = 35,
    device = Devices.PIXEL_7
)
@Composable
private fun BookshelfCardPreview() {
    PreviewTheme {
        val bookshelfFolder = BookshelfFolder(fakeSmbServer(), fakeFolder())
        BookshelfListItem(
            bookshelfFolder = bookshelfFolder,
            onClick = {},
            onInfoClick = {},
        )
    }
}
