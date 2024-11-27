package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.info.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ImageSync
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ShelvesSync
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookshelfInfoActionChips(
    isProgressScan: Boolean,
    enabled: Boolean,
    onScanClick: () -> Unit,
    onReThumbnailsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.targetSpacing),
        modifier = modifier
    ) {
        AssistChip(
            onClick = onScanClick,
            enabled = enabled && !isProgressScan,
            label = { Text(text = stringResource(id = R.string.bookshelf_info_btn_scan)) },
            leadingIcon = {
                if (isProgressScan) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                } else {
                    Icon(imageVector = ComicIcons.ShelvesSync, contentDescription = null)
                }
            }
        )
        AssistChip(
            onClick = onReThumbnailsClick,
            enabled = enabled,
            label = { Text(text = stringResource(id = R.string.bookshelf_info_btn_regenerate_thumbnails)) },
            leadingIcon = { Icon(imageVector = ComicIcons.ImageSync, contentDescription = null) }
        )
    }
}