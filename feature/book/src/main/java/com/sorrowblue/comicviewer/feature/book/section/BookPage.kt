package com.sorrowblue.comicviewer.feature.book.section

import com.sorrowblue.comicviewer.domain.model.file.Book

sealed interface PageItem {
    val key: String
}

data class NextPage(val isNext: Boolean, val nextBooks: List<NextBook>) : PageItem {

    override val key = "next:$isNext"
}

sealed interface NextBook {
    val book: Book

    data class Folder(override val book: Book) : NextBook
    data class Favorite(override val book: Book) : NextBook
}

sealed interface UnratedPage

sealed interface BookPage : PageItem {

    /** 読み取ったページをそのまま表示 */
    data class Default(val index: Int) : BookPage {
        override val key = "Default:$index"
    }

    /** 見開き 長辺が縦の場合次ページと結合 */
    sealed interface Spread : BookPage {
        val index: Int

        data class Unrated(override val index: Int) : Spread, UnratedPage {
            override val key = "Spread:$index"
        }

        data class Combine(override val index: Int, val nextIndex: Int) : Spread {
            override val key = "Spread:$index:$nextIndex"
        }

        data class Single(override val index: Int) : Spread {
            override val key = "Spread:$index"
        }

        data class Spread2(override val index: Int) : Spread {
            override val key = "Spread:$index"
        }
    }

    sealed interface Split : BookPage {
        val index: Int

        data class Unrated(override val index: Int) : Split, UnratedPage {
            override val key = "Split:$index"
        }

        data class Single(override val index: Int) : Split {
            override val key = "Split:$index"
        }

        data class Left(override val index: Int) : Split {
            override val key = "Split:$index"
        }

        data class Right(override val index: Int) : Split {
            override val key = "Split:$index:r"
        }
    }
}
