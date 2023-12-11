package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneScaffoldDirective
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter.source
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@Composable
private fun ListItem(
    overline: String,
    headline: String,
) {
    val colors = ListItemDefaults.colors(
        containerColor = Color.Transparent
    )
    ListItem(
        colors = colors,
        headlineContent = { Text(text = headline) },
        overlineContent = { Text(text = overline) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookshelfSideSheet(
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    bookshelfFolder: BookshelfFolder,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = "Close")
                    }
                },
                windowInsets = if (scaffoldDirective.maxHorizontalPartitions == 1) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                } else {
                    WindowInsets(0)
                },
                colors = if (scaffoldDirective.maxHorizontalPartitions == 1) TopAppBarDefaults.topAppBarColors()
                else TopAppBarDefaults.topAppBarColors(containerColor = ComicTheme.colorScheme.surfaceContainer)
            )
        },
        modifier = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            modifier
        } else {
            val padding = when (currentWindowAdaptiveInfo().windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> 16.dp
                WindowWidthSizeClass.Medium -> 24.dp
                WindowWidthSizeClass.Expanded -> 24.dp
                else -> 0.dp
            }
            modifier
                .padding(contentPadding)
                .padding(top = padding, bottom = padding, end = padding)
                .clip(ComicTheme.shapes.large)
        },
        contentWindowInsets = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            contentPadding.asWindowInsets()
        } else {
            WindowInsets(0)
        },
        containerColor = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            ComicTheme.colorScheme.surface
        } else {
            ComicTheme.colorScheme.surfaceContainer
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            val bookshelf = bookshelfFolder.bookshelf
            val folder = bookshelfFolder.folder
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = folder,
                    contentDescription = null,
                    placeholder = rememberDebugPlaceholder(),
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ComicTheme.colorScheme.surfaceContainerHighest),
                    contentScale = ContentScale.Crop
                )
                AssistChip(
                    onClick = { },
                    label = { Text(text = stringResource(id = bookshelf.source())) },
                )
            }
            ListItem(
                headline = bookshelf.displayName,
                overline = stringResource(id = R.string.bookshelf_info_label_display_name)
            )

            when (bookshelf) {
                is InternalStorage -> {
                    ListItem(
                        headline = folder.path,
                        overline = stringResource(id = R.string.bookshelf_info_label_path)
                    )
                }

                is SmbServer -> {
                    ListItem(
                        headline = bookshelf.host,
                        overline = stringResource(id = R.string.bookshelf_info_label_host)
                    )
                    ListItem(
                        headline = bookshelf.port.toString(),
                        overline = stringResource(id = R.string.bookshelf_info_label_port)
                    )
                    ListItem(
                        headline = folder.path,
                        overline = stringResource(id = R.string.bookshelf_info_label_path)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onRemoveClick) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_delete))
                    }
                }
                TextButton(onClick = onEditClick) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_edit))
                    }
                }
                TextButton(onClick = onScanClick) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_scan))
                    }
                }
            }
        }
    }
}
