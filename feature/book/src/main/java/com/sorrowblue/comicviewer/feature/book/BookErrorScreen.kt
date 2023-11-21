package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFaq
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults

@Composable
internal fun BookErrorScreen(
    uiState: BookScreenUiState.Error,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.name,
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2
                    )
                )
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets()
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                imageVector = ComicIcons.UndrawFaq,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (uiState.name.isEmpty()) "Unable to open" else "Unable to open \"${uiState.name}\"",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
