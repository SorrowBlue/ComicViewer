package com.sorrowblue.comicviewer.file.info

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.feature.file.info.R
import com.sorrowblue.comicviewer.file.info.navigation.FileInfoArgs
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder
import com.sorrowblue.comicviewer.framework.fold
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import logcat.logcat

sealed interface FileInfoScreenUiState {

    data object Hide : FileInfoScreenUiState

    data class Show(val file: File) : FileInfoScreenUiState
}

@HiltViewModel
internal class FileInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFileUseCase: GetFileUseCase,
) : ViewModel() {
    fun onAddReadLaterClick(file: File) {
        TODO("Not yet implemented")
    }

    private val args = FileInfoArgs(savedStateHandle)
    private val _file = MutableStateFlow<File?>(null)
    val file = _file.asStateFlow()

    init {
        getFileUseCase.execute(GetFileUseCase.Request(args.bookshelfId, args.path)).map {
            it.fold({ it }, { null })
        }.onEach {
            logcat { "_file.value=$it" }
            _file.value = it
        }.launchIn(viewModelScope)
    }
}

@Composable
internal fun FileInfoRoute(
    viewModel: FileInfoViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit
) {
    val file by viewModel.file.collectAsState()
    FileInfoScreen(
        file = file,
        onDismissRequest = onDismissRequest,
        onAddReadLaterClick = viewModel::onAddReadLaterClick,
        onAddFavoriteClick = onAddFavoriteClick,
        onOpenFolderClick = onOpenFolderClick
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FileInfoScreen(
    file: File?,
    onDismissRequest: () -> Unit = {},
    onAddReadLaterClick: (File) -> Unit = {},
    onAddFavoriteClick: (File) -> Unit = {},
    onOpenFolderClick: (File) -> Unit = {},
) {
    if (file == null) return
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
                        label = { Text(file.name.extension) },
                        modifier = Modifier.tooltipAnchor()
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text(file.size.asFileSize) })
                AssistChip(
                    onClick = {},
                    label = { Text(file.lastModifier.asDateTime) })
                if (file is Book) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(lastReadPage(file.lastPageRead, file.totalPageCount))
                        }
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                stringResource(
                                    id = R.string.file_info_label_last_read_time,
                                    file.lastReadTime.asDateTime
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

