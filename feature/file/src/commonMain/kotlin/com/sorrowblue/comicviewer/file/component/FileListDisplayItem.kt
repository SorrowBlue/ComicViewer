package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_list_label_switch_grid_view
import comicviewer.feature.file.generated.resources.file_list_label_switch_list_view
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun FileListDisplayItemState.fileListDisplayItem() {
    scope.clickableItem(
        onClick = ::onClick,
        icon = {
            Icon(
                if (fileListDisplay == FileListDisplay.Grid) {
                    ComicIcons.ViewList
                } else {
                    ComicIcons.GridView
                },
                null,
            )
        },
        label = {
            if (fileListDisplay == FileListDisplay.Grid) {
                stringResource(Res.string.file_list_label_switch_list_view)
            } else {
                stringResource(Res.string.file_list_label_switch_grid_view)
            }
        },
    )
}

@Composable
fun rememberFileListDisplayItemState(): FileListDisplayItemState {
    val useCase = rememberManageFolderDisplaySettingsUseCase()
    val coroutineScope = rememberCoroutineScope()
    return remember {
        FileListDisplayItemStateImpl(
            manageFolderDisplaySettingsUseCase = useCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
}

@Composable
fun FileListDisplayItem(modifier: Modifier = Modifier) {
    val state = rememberFileListDisplayItemState()
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier,
    ) {
        FileListDisplay.entries.forEachIndexed { index, option ->
            TonalToggleButton(
                colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                checked = option == state.fileListDisplay,
                onCheckedChange = {
                    if (it) {
                        state.onClick()
                    }
                },
                modifier = Modifier.semantics { role = Role.RadioButton },
                shapes =
                when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    FileListDisplay.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                Icon(
                    imageVector = when (option) {
                        FileListDisplay.Grid -> ComicIcons.GridView
                        FileListDisplay.List -> ComicIcons.ViewList
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

interface FileListDisplayItemState {
    val fileListDisplay: FileListDisplay

    fun onClick()
}

private class FileListDisplayItemStateImpl(
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var coroutineScope: CoroutineScope,
) : FileListDisplayItemState {
    override var fileListDisplay by mutableStateOf(FileListDisplay.Grid)

    init {
        manageFolderDisplaySettingsUseCase.settings
            .map { it.fileListDisplay }
            .distinctUntilChanged()
            .onEach {
                fileListDisplay = it
            }.launchIn(coroutineScope)
    }

    override fun onClick() {
        coroutineScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    fileListDisplay = when (it.fileListDisplay) {
                        FileListDisplay.Grid -> FileListDisplay.List
                        FileListDisplay.List -> FileListDisplay.Grid
                    },
                )
            }
        }
    }
}
