package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.ui.layout.ContentScale
import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.framework.ui.material3.Menu3

enum class PageScale(override val label: Int, val contentScale: ContentScale) : Menu3 {
    Fit(R.string.book_label_scale_fit, ContentScale.Fit),
    FillHeight(R.string.book_label_scale_fill_height, ContentScale.FillHeight),
    FillWidth(R.string.book_label_scale_fill_width, ContentScale.FillWidth),
    Inside(R.string.book_label_scale_inside, ContentScale.Inside),
    None(R.string.book_label_scale_none, ContentScale.None),
    FillBounds(R.string.book_label_scale_fill_bounds, ContentScale.FillBounds),
}
