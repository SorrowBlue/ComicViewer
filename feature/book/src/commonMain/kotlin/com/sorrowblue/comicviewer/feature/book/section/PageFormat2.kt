package com.sorrowblue.comicviewer.feature.book.section

import com.sorrowblue.comicviewer.framework.ui.material3.Menu3
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_label_display_format_default
import comicviewer.feature.book.generated.resources.book_label_display_format_split
import comicviewer.feature.book.generated.resources.book_label_display_format_splitspread
import comicviewer.feature.book.generated.resources.book_label_display_format_spread
import org.jetbrains.compose.resources.StringResource

enum class PageFormat2(override val label: StringResource) : Menu3 {
    Default(Res.string.book_label_display_format_default),
    Split(Res.string.book_label_display_format_split),
    Spread(Res.string.book_label_display_format_spread),
    SplitSpread(Res.string.book_label_display_format_splitspread),
}
