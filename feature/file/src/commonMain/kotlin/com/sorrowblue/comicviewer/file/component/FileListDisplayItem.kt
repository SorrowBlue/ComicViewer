package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.AppBarRowScope2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.platformGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_list_label_switch_grid_view
import comicviewer.feature.file.generated.resources.file_list_label_switch_list_view
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope2)
fun FileListDisplayItemState.fileListDisplayItem() {
    scope.clickableItem(
        onClick = ::onClick,
        icon = {
            Icon(
                if (fileListDisplay == FileListDisplay.Grid) ComicIcons.ViewList else ComicIcons.GridView,
                null
            )
        },
        label = {
            Text(
                if (fileListDisplay == FileListDisplay.Grid) {
                    stringResource(Res.string.file_list_label_switch_list_view)
                } else {
                    stringResource(Res.string.file_list_label_switch_grid_view)
                }
            )
        },
        autoDismiss = false
    )
}

@Composable
fun rememberFileListDisplayItemState(): FileListDisplayItemState {
    val factory = LocalPlatformContext.current.platformGraph as FileListDisplayItemGraph.Factory
    val graph = rememberRetained { factory.createFileListDisplayItemGraph() }
    val coroutineScope = rememberCoroutineScope()
    return remember {
        FileListDisplayItemStateImpl(
            manageFolderDisplaySettingsUseCase = graph.manageFolderDisplaySettingsUseCase,
            coroutineScope = coroutineScope
        )
    }.apply {
        this.coroutineScope = coroutineScope
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
        manageFolderDisplaySettingsUseCase.settings.map { it.fileListDisplay }
            .distinctUntilChanged().onEach {
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
                    }
                )
            }
        }

    }
}

@Scope
annotation class FileListDisplayItemScope

@GraphExtension(FileListDisplayItemScope::class)
interface FileListDisplayItemGraph {

    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createFileListDisplayItemGraph(): FileListDisplayItemGraph
    }
}
