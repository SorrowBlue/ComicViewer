package com.sorrowblue.comicviewer.feature.book.menu

import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageScale

internal data class BookMenuScreenUiState(
    val pageFormat2: PageFormat2 = PageFormat2.Default,
    val pageScale: PageScale = PageScale.Fit,
)
