/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.book

import com.sorrowblue.comicviewer.domain.model.file.Book

/**
 * Base interface representing a page item displayed in the viewer.
 */
sealed interface PageItem {
    val key: String
}

/**
 * Navigation transition page to the previous or next book.
 *
 * @property isNext `true` if navigating to the next book, `false` if navigating to the previous book.
 * @property nextBooks Candidate books to transition to.
 */
data class NextPage(val isNext: Boolean, val nextBooks: List<NextBook>) : PageItem {
    override val key: String get() = "next:$isNext"
}

/**
 * Represents a previous or next book candidate.
 */
sealed interface NextBook {
    val book: Book

    /** Previous or next book within a folder. */
    data class Folder(override val book: Book) : NextBook

    /** Previous or next book within a collection. */
    data class Collection(override val book: Book) : NextBook
}

/**
 * Marker interface for a page whose image aspect ratio has not yet been evaluated and whose layout is pending.
 */
sealed interface UnratedPage

/**
 * Represents an individual page item of a comic book.
 */
sealed interface BookPage : PageItem {

    /**
     * Default display format that renders each page as-is.
     *
     * @property index Zero-based page index.
     */
    data class Default(val index: Int) : BookPage {
        override val key: String get() = "Default:$index"
    }

    /**
     * Two-page spread display format. Portrait pages are paired with adjacent pages, while landscape pages occupy a full spread.
     */
    sealed interface Spread : BookPage {
        val index: Int

        /** Page whose aspect ratio has not yet been evaluated. */
        data class Unrated(override val index: Int) :
            Spread,
            UnratedPage {
            override val key: String get() = "Spread:unrated:$index"
        }

        /** Two portrait pages combined into a single spread. */
        data class Combine(override val index: Int, val nextIndex: Int) : Spread {
            override val key: String get() = "Spread:combine:$index:$nextIndex"
        }

        /** Single page (such as a cover page or an unpaired portrait page). */
        data class Single(override val index: Int) : Spread {
            override val key: String get() = "Spread:single:$index"
        }

        /** Single landscape page displayed as a full spread. */
        data class Spread2(override val index: Int) : Spread {
            override val key: String get() = "Spread:spread2:$index"
        }
    }

    /**
     * Split display format where landscape images are split into right and left halves.
     */
    sealed interface Split : BookPage {
        val index: Int

        /** Page whose aspect ratio has not yet been evaluated. */
        data class Unrated(override val index: Int) :
            Split,
            UnratedPage {
            override val key: String get() = "Split:unrated:$index"
        }

        /** Single portrait page that does not require splitting. */
        data class Single(override val index: Int) : Split {
            override val key: String get() = "Split:single:$index"
        }

        /** Left half of a landscape page. */
        data class Left(override val index: Int) : Split {
            override val key: String get() = "Split:left:$index"
        }

        /** Right half of a landscape page. */
        data class Right(override val index: Int) : Split {
            override val key: String get() = "Split:right:$index"
        }
    }
}
