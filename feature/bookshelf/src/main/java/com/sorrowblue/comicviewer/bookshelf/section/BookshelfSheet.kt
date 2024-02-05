package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfConverter.source
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.OptionButton
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookshelfInfoSheet(
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    bookshelfFolder: BookshelfFolder,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtraPaneScaffold(
        topBar = {
            ExtraPaneScaffoldDefault.TopAppBar(
                title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
                onCloseClick = onCloseClick,
                scaffoldDirective = scaffoldDirective
            )
        },
        modifier = modifier,
        contentPadding = contentPadding,
        scaffoldDirective = scaffoldDirective
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
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            OptionButton(
                text = R.string.bookshelf_action_delete,
                icon = ComicIcons.Delete,
                onClick = onRemoveClick
            )
            OptionButton(
                text = R.string.bookshelf_action_edit,
                icon = ComicIcons.Edit,
                onClick = onEditClick
            )
            OptionButton(
                text = R.string.bookshelf_action_scan,
                icon = ComicIcons.Refresh,
                onClick = onScanClick
            )
        }
    }
}

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
