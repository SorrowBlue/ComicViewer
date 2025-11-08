package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.ui.layout.ContentScale
import com.sorrowblue.comicviewer.framework.ui.material3.ExposedDropdownMenu
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_label_scale_fill_bounds
import comicviewer.feature.book.generated.resources.book_label_scale_fill_height
import comicviewer.feature.book.generated.resources.book_label_scale_fill_width
import comicviewer.feature.book.generated.resources.book_label_scale_fit
import comicviewer.feature.book.generated.resources.book_label_scale_inside
import comicviewer.feature.book.generated.resources.book_label_scale_none
import org.jetbrains.compose.resources.StringResource

enum class PageScale(override val label: StringResource, val contentScale: ContentScale) : ExposedDropdownMenu {
    Fit(Res.string.book_label_scale_fit, ContentScale.Fit),
    FillHeight(Res.string.book_label_scale_fill_height, ContentScale.FillHeight),
    FillWidth(Res.string.book_label_scale_fill_width, ContentScale.FillWidth),
    Inside(Res.string.book_label_scale_inside, ContentScale.Inside),
    None(Res.string.book_label_scale_none, ContentScale.None),
    FillBounds(Res.string.book_label_scale_fill_bounds, ContentScale.FillBounds),
}
