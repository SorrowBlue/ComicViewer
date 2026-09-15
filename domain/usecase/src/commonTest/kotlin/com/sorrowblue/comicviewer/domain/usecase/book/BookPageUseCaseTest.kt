/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class BookPageUseCaseTest {

    private val createInitialUseCase = CreateInitialBookPagesUseCase()
    private val resolveUseCase = ResolveBookPageLayoutUseCase()

    @Test
    fun createInitialBookPagesUseCase_delegatesToService() {
        val pages = createInitialUseCase(
            totalPageCount = 2,
            pageFormat = BookSettings.PageFormat.Spread,
            isCompactWindow = false,
        )
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Unrated(1),
            ),
            pages,
        )
    }

    @Test
    fun resolveBookPageLayoutUseCase_delegatesToService() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
        )

        val updated = resolveUseCase(
            currentList = initialList,
            unratedPage = BookPage.Spread.Unrated(0),
            isPortrait = true,
        )

        assertEquals(
            listOf(
                BookPage.Spread.Single(0),
                BookPage.Spread.Unrated(1),
            ),
            updated,
        )
    }
}
