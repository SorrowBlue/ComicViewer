package com.sorrowblue.comicviewer.file

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.component.forwardingPainter
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.OptionButton
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox2
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun FileInfoSheet(
    file: File,
    onCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOpenFolderClick: () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    ExtraPaneScaffold(
        topBar = {
            ExtraPaneScaffoldDefault.TopAppBar(
                title = { Text(text = stringResource(id = R.string.file_title)) },
                onCloseClick = onCloseClick,
                scaffoldDirective = scaffoldDirective
            )
        },
        contentPadding = contentPadding,
        scaffoldDirective = scaffoldDirective,
        modifier = modifier
    ) {
        Row {
            AsyncImage(
                model = file,
                contentDescription = null,
                placeholder = rememberDebugPlaceholder(),
                error = forwardingPainter(
                    rememberVectorPainter(if (file is Book) ComicIcons.Book else ComicIcons.Folder),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(Modifier.padding(start = ComicTheme.dimension.padding * 4)) {
                Text(
                    text = file.name,
                    style = ComicTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(ComicTheme.dimension.padding * 2))
                Text(
                    text = file.parent,
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding * 2))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding)
        ) {
            if (file is Book) {
                PlainTooltipBox2(
                    tooltipContent = { Text(stringResource(id = R.string.file_label_file_extension)) }
                ) { state ->
                    AssistChip(
                        onClick = { scope.launch { state.show() } },
                        label = { Text(file.name.extension) }
                    )
                }
            }
            if (0 < file.size) {
                AssistChip(
                    onClick = {},
                    label = { Text(text = file.size.asFileSize) }
                )
            }
            PlainTooltipBox2(
                tooltipContent = {
                    Text(text = stringResource(R.string.file_label_modified_date))
                }
            ) { state ->
                AssistChip(
                    onClick = { scope.launch { state.show() } },
                    label = { Text(text = file.lastModifier.asDateTime) }
                )
            }
            if (file is Book) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.file_text_page_count,
                                file.lastPageRead,
                                file.totalPageCount
                            )
                        )
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.file_info_label_last_read_time,
                                file.lastReadTime.asDateTime
                            )
                        )
                    }
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
                text = R.string.file_info_label_add_read_later,
                icon = ComicIcons.WatchLater,
                onClick = onReadLaterClick
            )
            OptionButton(
                text = R.string.file_info_label_add_favourites,
                icon = ComicIcons.Favorite,
                onClick = onFavoriteClick
            )
            OptionButton(
                text = R.string.file_info_label_open_folder,
                icon = ComicIcons.FolderOpen,
                onClick = onOpenFolderClick
            )
        }
    }
}
