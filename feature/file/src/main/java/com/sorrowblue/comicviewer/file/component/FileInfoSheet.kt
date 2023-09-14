package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.info.Converter
import com.sorrowblue.comicviewer.file.info.Converter.extension
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder

sealed interface FileInfoSheetUiState {

    data object Hide : FileInfoSheetUiState

    data class Show(val file: File) : FileInfoSheetUiState
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FileInfoSheet(
    uiState: FileInfoSheetUiState,
    onDismissRequest: () -> Unit = {},
    onAddReadLaterClick: (File) -> Unit = {},
    onAddFavoriteClick: (File) -> Unit = {},
    onOpenFolderClick: (File) -> Unit = {},
) {
    if (uiState is FileInfoSheetUiState.Show) {
        val file = uiState.file
        val sheetState = rememberModalBottomSheetState(true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(AppMaterialTheme.dimens.margin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = file,
                    contentDescription = when (file) {
                        is Book -> "book thumbnail"
                        is Folder -> "folder thumbnail"
                    },
                    modifier = Modifier.size(150.dp),
                    placeholder = debugPlaceholder()
                )
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = file.parent,
                    modifier = Modifier.padding(top = AppMaterialTheme.dimens.spacer),
                    style = MaterialTheme.typography.labelSmall
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppMaterialTheme.dimens.spacer),
                    horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer)
                ) {
                    PlainTooltipBox(tooltip = {
                        Text("ファイルの拡張子")
                    }) {
                        AssistChip(
                            onClick = {},
                            label = { Text(file.name.extension().orEmpty()) },
                            modifier = Modifier.tooltipAnchor()
                        )
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text(Converter.fileSize(file.size)) })
                    AssistChip(
                        onClick = {},
                        label = { Text(Converter.dateTime(file.lastModifier)) })
                    if (file is Book) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(Converter.lastReadPage(file.lastPageRead, file.totalPageCount))
                            }
                        )
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    stringResource(
                                        id = R.string.file_info_label_last_read_time,
                                        Converter.dateTime(file.lastReadTime)
                                    )
                                )
                            }
                        )
                    }
                }
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = AppMaterialTheme.dimens.spacer),
                    horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer)
                ) {
                    FilledTonalButton(onClick = { onAddReadLaterClick(file) }) {
                        Text(stringResource(id = R.string.file_info_label_add_read_later))
                    }
                    FilledTonalButton(onClick = { onAddFavoriteClick(file) }) {
                        Text(stringResource(id = R.string.file_info_label_add_favourites))
                    }
                    FilledTonalButton(onClick = { onOpenFolderClick(file) }) {
                        Text(stringResource(id = R.string.file_info_label_open_folder))
                    }
                }
            }
        }
    }
}

