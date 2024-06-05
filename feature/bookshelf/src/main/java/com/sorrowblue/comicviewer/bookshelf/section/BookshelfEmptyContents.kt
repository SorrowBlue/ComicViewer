package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo

@Composable
internal fun BookshelfEmptyContents(innerPadding: PaddingValues) {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ComicIcons.UndrawBookshelves,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxSize(0.5f)
            )
            Text(
                text = stringResource(
                    id = com.sorrowblue.comicviewer.feature.bookshelf.R.string.bookshelf_list_message_no_bookshelves_added_yet
                ),
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            shape = ComicTheme.shapes.large
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = ComicIcons.UndrawBookshelves,
                    contentDescription = null,
                    modifier = Modifier
                        .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                        .fillMaxSize(0.5f)
                )
                Text(
                    text = stringResource(
                        id = com.sorrowblue.comicviewer.feature.bookshelf.R.string.bookshelf_list_message_no_bookshelves_added_yet
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
