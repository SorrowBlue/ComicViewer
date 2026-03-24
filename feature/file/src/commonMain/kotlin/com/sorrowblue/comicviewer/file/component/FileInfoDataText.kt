package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun FileInfoDataText(
    overlineContent: @Composable () -> Unit,
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ProvideTextStyle(ComicTheme.typography.labelSmall) {
            overlineContent()
        }
        ProvideTextStyle(ComicTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)) {
            headlineContent()
        }
    }
}
