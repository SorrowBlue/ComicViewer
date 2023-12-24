package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Composable
internal fun WelcomeSheet(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(contentPadding)
            .padding(ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
            Image(
                imageVector = ComicIcons.Launcher,
                contentDescription = null,
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(0.5f),
            )
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.app_name),
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@PreviewComic
@Composable
private fun PreviewWelcomeSheet() {
    PreviewTheme {
        Surface {
            WelcomeSheet(
                contentPadding = PaddingValues()
            )
        }
    }
}
