package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class FolderDisplaySettings(
    val display: Display = Display.GRID,
    val spanCount: Int = 3,
    val sort: Sort = Sort.NAME,
    val order: Order = Order.ASC
) {

    enum class Display {
        GRID, LIST
    }

    enum class Order {
        ASC, DESC
    }

    enum class Sort {
        NAME, DATE, SIZE
    }
}
