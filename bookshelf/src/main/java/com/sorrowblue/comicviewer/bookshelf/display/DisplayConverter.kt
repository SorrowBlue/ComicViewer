package com.sorrowblue.comicviewer.bookshelf.display

import androidx.databinding.InverseMethod
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings

internal object DisplayConverter {

    @InverseMethod("intToDisplay")
    @JvmStatic
    fun displayToInt(value: BookshelfDisplaySettings.Display): Int {
        return when (value) {
            BookshelfDisplaySettings.Display.GRID -> R.id.view_type_grid
            BookshelfDisplaySettings.Display.LIST -> R.id.view_type_list
        }
    }

    @JvmStatic
    fun intToDisplay(value: Int): BookshelfDisplaySettings.Display {
        return when (value) {
            R.id.view_type_grid -> BookshelfDisplaySettings.Display.GRID
            R.id.view_type_list -> BookshelfDisplaySettings.Display.LIST
            else -> BookshelfDisplaySettings.DEFAULT_DISPLAY
        }
    }
}
