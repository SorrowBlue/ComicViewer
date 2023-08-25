package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class FolderDisplaySettings(
    val display: Display = Display.GRID,
    val isEnabledThumbnail: Boolean = true,
    val spanCount: Int = 3,
    val columnSize: Size = Size.MEDIUM,
    val sortType: SortType = SortType.NAME(true)
) {

    enum class Size {
        SMALL, MEDIUM, LARGE
    }

    enum class Display {
        GRID, LIST
    }
}
