package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ImageSync
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ShelvesSync
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_delete
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_edit
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_scan_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_scan_thumbnail
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookshelfInfoButtons(
    isScanningFile: Boolean,
    isScanningThumbnail: Boolean,
    onScanFileClick: () -> Unit,
    onScanThumbnailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Button(
            onClick = onEditClick,
            shape = ButtonDefaults.mediumPressedShape,
            modifier = Modifier.fillMaxWidth()
                .heightIn(min = ButtonDefaults.MediumContainerHeight)
                .testTag("EditButton")
        ) {
            Icon(
                imageVector = ComicIcons.Edit,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.MediumIconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.MediumIconSpacing))
            Text(stringResource(Res.string.bookshelf_info_btn_edit))
        }
        Spacer(Modifier.size(ComicTheme.dimension.targetSpacing))
        Row {
            OutlinedButton(
                onClick = onScanFileClick,
                shape = ButtonDefaults.mediumPressedShape,
                enabled = !isScanningFile && !isScanningThumbnail,
                modifier = Modifier.weight(1f).heightIn(min = ButtonDefaults.MediumContainerHeight),
            ) {
                if (isScanningFile) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                    )
                } else {
                    Icon(
                        ComicIcons.ShelvesSync,
                        null,
                        tint = ComicTheme.colorScheme.secondary,
                        modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                    )
                }
                Spacer(Modifier.size(ButtonDefaults.MediumIconSpacing))
                Text(stringResource(Res.string.bookshelf_info_btn_scan_file))
            }
            Spacer(Modifier.size(ComicTheme.dimension.targetSpacing))
            OutlinedButton(
                onClick = onScanThumbnailClick,
                enabled = !isScanningFile && !isScanningThumbnail,
                shape = ButtonDefaults.mediumPressedShape,
                modifier = Modifier.weight(1f).heightIn(min = ButtonDefaults.MediumContainerHeight),
            ) {
                if (isScanningThumbnail) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                    )
                } else {
                    Icon(
                        ComicIcons.ImageSync,
                        null,
                        tint = ComicTheme.colorScheme.tertiary,
                        modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                    )
                }
                Spacer(Modifier.size(ButtonDefaults.MediumIconSpacing))
                Text(stringResource(Res.string.bookshelf_info_btn_scan_thumbnail))
            }
        }
        Spacer(Modifier.size(ComicTheme.dimension.targetSpacing))
        OutlinedButton(
            onClick = onDeleteClick,
            shape = ButtonDefaults.mediumPressedShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ButtonDefaults.MediumContainerHeight)
                .testTag("DeleteButton"),
        ) {
            Icon(
                imageVector = ComicIcons.Delete,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.MediumIconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.MediumIconSpacing))
            Text(stringResource(Res.string.bookshelf_info_btn_delete))
        }
    }
}
