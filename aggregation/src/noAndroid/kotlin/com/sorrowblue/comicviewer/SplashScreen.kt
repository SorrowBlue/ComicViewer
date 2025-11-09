package com.sorrowblue.comicviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun SplashScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                imageVector = ComicIcons.Launcher,
                contentDescription = null,
                modifier = Modifier.size(140.dp),
            )
        }
    }
}

@Composable
@Preview
private fun SplashScreenPreview() {
    PreviewTheme {
        SplashScreen()
    }
}
