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
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ImageSync
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ShelvesSync
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_scan_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_scan_thumbnail
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookshelfInfoActionChips(
    isScanningFile: Boolean,
    isScanningThumbnail: Boolean,
    onScanFileClick: () -> Unit,
    onScanThumbnailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.targetSpacing),
        modifier = modifier
    ) {
        AssistChip(
            onClick = onScanFileClick,
            enabled = !isScanningFile && !isScanningThumbnail,
            label = { Text(text = stringResource(Res.string.bookshelf_info_btn_scan_file)) },
            leadingIcon = {
                if (isScanningFile) {
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
            onClick = onScanThumbnailClick,
            enabled = !isScanningFile && !isScanningThumbnail,
            label = { Text(text = stringResource(Res.string.bookshelf_info_btn_scan_thumbnail)) },
            leadingIcon = {
                if (isScanningThumbnail) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                } else {
                    Icon(imageVector = ComicIcons.ImageSync, contentDescription = null)
                }
            }
        )
    }
}
