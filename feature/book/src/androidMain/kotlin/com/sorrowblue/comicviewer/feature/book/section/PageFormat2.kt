package com.sorrowblue.comicviewer.feature.book.section

import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.framework.ui.material3.Menu3

enum class PageFormat2(override val label: Int) : Menu3 {
    Default(R.string.book_label_display_format_default),
    Split(R.string.book_label_display_format_split),
    Spread(R.string.book_label_display_format_spread),
    SplitSpread(R.string.book_label_display_format_splitspread),
}
