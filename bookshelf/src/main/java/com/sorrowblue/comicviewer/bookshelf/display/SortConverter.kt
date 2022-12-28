package com.sorrowblue.comicviewer.bookshelf.display

import androidx.databinding.InverseMethod
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings

internal object SortConverter {

    @InverseMethod("intToSort")
    @JvmStatic
    fun sortToInt(value: BookshelfDisplaySettings.Sort): Int {
        return when (value) {
            BookshelfDisplaySettings.Sort.NAME -> R.id.sort_type_name
            BookshelfDisplaySettings.Sort.DATE -> R.id.sort_type_date
            BookshelfDisplaySettings.Sort.SIZE -> R.id.sort_type_size
        }
    }

    @JvmStatic
    fun intToSort(value: Int): BookshelfDisplaySettings.Sort {
        return when (value) {
            R.id.sort_type_name -> BookshelfDisplaySettings.Sort.NAME
            R.id.sort_type_date -> BookshelfDisplaySettings.Sort.DATE
            R.id.sort_type_size -> BookshelfDisplaySettings.Sort.SIZE
            else -> BookshelfDisplaySettings.DEFAULT_SORT
        }
    }
}
