package com.sorrowblue.comicviewer.bookshelf.display

import androidx.databinding.InverseMethod
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings

internal object OrderConverter {

    @InverseMethod("intToOrder")
    @JvmStatic
    fun orderToInt(value: BookshelfDisplaySettings.Order): Int {
        return when (value) {
            BookshelfDisplaySettings.Order.ASC -> R.id.order_type_asc
            BookshelfDisplaySettings.Order.DESC -> R.id.order_type_desc
        }
    }

    @JvmStatic
    fun intToOrder(value: Int): BookshelfDisplaySettings.Order {
        return when (value) {
            R.id.order_type_asc -> BookshelfDisplaySettings.Order.ASC
            R.id.order_type_desc -> BookshelfDisplaySettings.Order.DESC
            else -> BookshelfDisplaySettings.DEFAULT_ORDER
        }
    }
}
