/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.toggleableItem
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_action_show_hidden
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun HiddenFilesToggleableItemState.hiddenFilesToggleableItem() {
    scope.toggleableItem(
        checked = showHiddenFile,
        onCheckedChange = ::onCheckedChange,
        icon = {
            Icon(ComicIcons.RemoveRedEye, null)
        },
        label = {
            stringResource(Res.string.file_action_show_hidden)
        },
    )
}

@Composable
fun rememberHiddenFilesToggleableItemState(
    viewModel: HiddenFilesToggleableItemViewModel = metroViewModel(),
): HiddenFilesToggleableItemState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        HiddenFilesToggleableItemStateImpl(
            coroutineScope = coroutineScope,
            hiddenFilesFlow = viewModel.hiddenFilesFlow,
            setHiddenFiles = viewModel::setHiddenFiles,
        )
    }
}

interface HiddenFilesToggleableItemState {
    val showHiddenFile: Boolean

    fun onCheckedChange(checked: Boolean)
}

private class HiddenFilesToggleableItemStateImpl(
    coroutineScope: CoroutineScope,
    hiddenFilesFlow: SharedFlow<Boolean>,
    private val setHiddenFiles: (Boolean) -> Unit,
) : HiddenFilesToggleableItemState {

    override var showHiddenFile: Boolean by mutableStateOf(false)

    init {
        hiddenFilesFlow.onEach { showHiddenFile = it }.launchIn(coroutineScope)
    }

    override fun onCheckedChange(checked: Boolean) {
        setHiddenFiles(checked)
    }
}

@ViewModelKey
@ContributesIntoMap(AppScope::class)
class HiddenFilesToggleableItemViewModel(
    private val settingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    val hiddenFilesFlow =
        settingsUseCase.settings.map { it.showHiddenFiles }.distinctUntilChanged()
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    fun setHiddenFiles(value: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(showHiddenFiles = value)
            }
        }
    }
}
