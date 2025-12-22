package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.toggleableItem
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_action_show_hidden
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun HiddenFilesToggleableItemState.hiddenFilesToggleableItem() {
    scope.toggleableItem(
        checked = showHiddenFile,
        onCheckedChange = ::onCheckedChange,
        icon = { Icon(ComicIcons.FolderOff, null) },
        label = { stringResource(Res.string.file_action_show_hidden) }
    )
}

@Composable
fun rememberHiddenFilesToggleableItemState(): HiddenFilesToggleableItemState {
    val useCase = rememberManageFolderDisplaySettingsUseCase()
    val coroutineScope = rememberCoroutineScope()
    return remember {
        HiddenFilesToggleableItemStateImpl(
            manageFolderDisplaySettingsUseCase = useCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
}

interface HiddenFilesToggleableItemState {
    val showHiddenFile: Boolean

    fun onCheckedChange(checked: Boolean)
}

private class HiddenFilesToggleableItemStateImpl(
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var coroutineScope: CoroutineScope,
) : HiddenFilesToggleableItemState {
    override var showHiddenFile: Boolean by mutableStateOf(false)

    init {
        manageFolderDisplaySettingsUseCase.settings
            .map { it.showHiddenFiles }
            .distinctUntilChanged()
            .onEach {
                showHiddenFile = it
            }.launchIn(coroutineScope)
    }

    override fun onCheckedChange(checked: Boolean) {
        coroutineScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(showHiddenFiles = checked)
            }
        }
    }
}

@Scope
annotation class HiddenFilesToggleItemScope

@GraphExtension(HiddenFilesToggleItemScope::class)
interface HiddenFilesToggleableItemGraph {
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createHiddenFilesToggleableItemGraph(): HiddenFilesToggleableItemGraph
    }
}
