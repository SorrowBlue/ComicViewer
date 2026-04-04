package com.sorrowblue.comicviewer.feature.settings.display

import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal data class DarkModeScreenUiState(
    val list: ImmutableList<DarkMode> = DarkMode.entries.toImmutableList(),
    val darkMode: DarkMode = DarkMode.DEVICE,
)
