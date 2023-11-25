package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.ui.layout.ContentScale
import com.sorrowblue.comicviewer.feature.book.Menu3
import com.sorrowblue.comicviewer.feature.book.R

enum class PageScale(override val label: Int, val contentScale: ContentScale) : Menu3 {
    Fit(R.string.book_label_scale_fit, ContentScale.Fit),
    FillHeight(R.string.book_label_scale_fill_height, ContentScale.FillHeight),
    FillWidth(R.string.book_label_scale_fill_width, ContentScale.FillWidth),
    Inside(R.string.book_label_scale_inside, ContentScale.Inside),
    None(R.string.book_label_scale_none, ContentScale.None),
    FillBounds(R.string.book_label_scale_fill_bounds, ContentScale.FillBounds)
}
