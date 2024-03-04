package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class FolderDisplaySettings(
    val display: Display = Display.GRID,
    val isEnabledThumbnail: Boolean = true,
    val spanCount: Int = 3,
    val columnSize: Size = Size.MEDIUM,
    val sortType: SortType = SortType.NAME(true),
    val showHidden: Boolean = false,
) {

    enum class Size {
        MEDIUM, LARGE
    }

    enum class Display {
        GRID, LIST
    }
}
