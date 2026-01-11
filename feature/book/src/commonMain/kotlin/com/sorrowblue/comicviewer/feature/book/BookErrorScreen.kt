package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.Plugin
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFaq
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.tokens.ElevationTokens
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_text_could_not_open
import comicviewer.feature.book.generated.resources.book_text_could_not_open_name
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookErrorScreen(uiState: BookScreenUiState.Error, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ComicIcons.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2,
                    ),
                ),
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                ),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Image(
                imageVector = ComicIcons.UndrawFaq,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (uiState.name.isEmpty()) {
                    stringResource(Res.string.book_text_could_not_open)
                } else {
                    stringResource(Res.string.book_text_could_not_open_name, uiState.name)
                },
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable
internal fun BookPluginErrorScreen(
    uiState: BookScreenUiState.PluginError,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ComicIcons.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2,
                    ),
                ),
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                ),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                painter = rememberVectorPainter(image = ComicIcons.Plugin),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                text = uiState.error,
                style = ComicTheme.typography.bodyLarge,
            )
        }
    }
}
