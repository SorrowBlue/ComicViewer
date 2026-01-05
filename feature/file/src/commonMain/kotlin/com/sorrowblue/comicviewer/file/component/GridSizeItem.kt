package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_action_change_grid_size
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun GridSizeItemState.gridSizeItem() {
    if (this.fileListDisplay == FileListDisplay.Grid) {
        scope.clickableItem(
            icon = { Icon(ComicIcons.Grid4x4, null) },
            label = { stringResource(Res.string.file_action_change_grid_size) },
            onClick = ::onClick,
        )
    }
}

@Composable
fun rememberGridSizeItemState(): GridSizeItemState {
    val useCase = rememberManageFolderDisplaySettingsUseCase()
    val coroutineScope = rememberCoroutineScope()
    return remember {
        GridSizeItemStateImpl(
            manageFolderDisplaySettingsUseCase = useCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
}

interface GridSizeItemState {
    val fileListDisplay: FileListDisplay

    fun onClick()
}

private class GridSizeItemStateImpl(
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var coroutineScope: CoroutineScope,
) : GridSizeItemState {
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
                    gridColumnSize = when (it.gridColumnSize) {
                        GridColumnSize.Medium -> GridColumnSize.Large
                        GridColumnSize.Large -> GridColumnSize.Medium
                    },
                )
            }
        }
    }
}

@Scope
annotation class GridSizeItemScope
