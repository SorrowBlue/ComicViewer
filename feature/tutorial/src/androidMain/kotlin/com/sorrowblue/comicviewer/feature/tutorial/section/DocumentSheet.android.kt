package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@PreviewMultiScreen
@Composable
private fun DocumentSheetPreview() {
    PreviewTheme {
        Surface {
            DocumentSheet(
                uiState = DocumentSheetUiState.INSTALLED,
                onDownloadClick = {},
                contentPadding = PaddingValues()
            )
        }
    }
}
