package com.sorrowblue.comicviewer.feature.authentication.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun HeaderContents(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ComicIcons.Key,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = "ComicViewer authentication",
            style = ComicTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )
    }
}
