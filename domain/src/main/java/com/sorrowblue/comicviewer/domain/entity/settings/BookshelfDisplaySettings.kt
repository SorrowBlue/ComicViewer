package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class BookshelfDisplaySettings(
    val display: Display = DEFAULT_DISPLAY,
    val spanCount: Int = DEFAULT_SPAN_COUNT,
    val sort: Sort = DEFAULT_SORT,
    val order: Order = DEFAULT_ORDER
) {

    companion object {
        val DEFAULT_DISPLAY = Display.GRID
        val DEFAULT_SORT = Sort.NAME
        val DEFAULT_ORDER = Order.ASC
        const val DEFAULT_SPAN_COUNT = 3
    }

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
