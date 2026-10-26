/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookPageLayoutServiceTest {

    private val service = BookPageLayoutServiceImpl()

    @Test
    fun createInitialPages_withZeroOrNegativeCount_returnsEmptyList() {
        assertTrue(service.createInitialPages(0, BookSettings.PageFormat.Default, false).isEmpty())
        assertTrue(service.createInitialPages(-1, BookSettings.PageFormat.Spread, false).isEmpty())
    }

    @Test
    fun createInitialPages_defaultFormat_returnsDefaultPages() {
        val pages = service.createInitialPages(3, BookSettings.PageFormat.Default, false)
        assertEquals(
            listOf(
                BookPage.Default(0),
                BookPage.Default(1),
                BookPage.Default(2),
            ),
            pages,
        )
    }

    @Test
    fun createInitialPages_spreadFormat_returnsSpreadUnratedPages() {
        val pages = service.createInitialPages(3, BookSettings.PageFormat.Spread, false)
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Unrated(1),
                BookPage.Spread.Unrated(2),
            ),
            pages,
        )
    }

    @Test
    fun createInitialPages_splitFormat_returnsSplitUnratedPages() {
        val pages = service.createInitialPages(3, BookSettings.PageFormat.Split, false)
        assertEquals(
            listOf(
                BookPage.Split.Unrated(0),
                BookPage.Split.Unrated(1),
                BookPage.Split.Unrated(2),
            ),
            pages,
        )
    }

    @Test
    fun createInitialPages_autoFormat_resolvesBasedOnCompactWindow() {
        val compactPages = service.createInitialPages(2, BookSettings.PageFormat.Auto, true)
        assertEquals(
            listOf(
                BookPage.Split.Unrated(0),
                BookPage.Split.Unrated(1),
            ),
            compactPages,
        )

        val nonCompactPages = service.createInitialPages(2, BookSettings.PageFormat.Auto, false)
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Unrated(1),
            ),
            nonCompactPages,
        )
    }

    @Test
    fun resolveSplitPage_portraitImage_resolvesToSingle() {
        val initialList: List<PageItem> = listOf(
            BookPage.Split.Unrated(0),
            BookPage.Split.Unrated(1),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Split.Unrated(0),
            isPortrait = true,
        )

        assertEquals(
            listOf(
                BookPage.Split.Single(0),
                BookPage.Split.Unrated(1),
            ),
            updated,
        )
    }

    @Test
    fun resolveSplitPage_landscapeImage_splitsIntoRightAndLeft() {
        val initialList: List<PageItem> = listOf(
            BookPage.Split.Unrated(0),
            BookPage.Split.Unrated(1),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Split.Unrated(0),
            isPortrait = false,
        )

        assertEquals(
            listOf(
                BookPage.Split.Right(0),
                BookPage.Split.Left(0),
                BookPage.Split.Unrated(1),
            ),
            updated,
        )
    }

    @Test
    fun resolveSpreadPage_coverPage_remainsSingleEvenIfPortrait() {
        val initialList: List<PageItem> = listOf(
            NextPage(false, emptyList()),
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            NextPage(true, emptyList()),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Spread.Unrated(0),
            isPortrait = true,
        )

        assertEquals(
            listOf(
                NextPage(false, emptyList()),
                BookPage.Spread.Single(0),
                BookPage.Spread.Unrated(1),
                NextPage(true, emptyList()),
            ),
            updated,
        )
    }

    @Test
    fun resolveSpreadPage_consecutivePortraitPages_combinesIntoSpread() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            BookPage.Spread.Unrated(2),
        )

        val afterPage0 = service.resolvePage(initialList, BookPage.Spread.Unrated(0), true)
        val afterPage1 = service.resolvePage(afterPage0, BookPage.Spread.Unrated(1), true)
        val afterPage2 = service.resolvePage(afterPage1, BookPage.Spread.Unrated(2), true)

        assertEquals(
            listOf(
                BookPage.Spread.Single(0),
                BookPage.Spread.Combine(1, 2),
            ),
            afterPage2,
        )
    }

    @Test
    fun resolveSpreadPage_landscapePage_becomesSpread2() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Spread.Unrated(1),
            isPortrait = false,
        )

        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Spread2(1),
            ),
            updated,
        )
    }

    @Test
    fun resolveSpreadPage_outOfOrderLoading_combinesCorrectly() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            BookPage.Spread.Unrated(2),
        )

        // Page 2 loads first (portrait)
        val afterPage2 = service.resolvePage(initialList, BookPage.Spread.Unrated(2), true)
        // Page 1 loads second (portrait)
        val afterPage1 = service.resolvePage(afterPage2, BookPage.Spread.Unrated(1), true)

        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Combine(1, 2),
            ),
            afterPage1,
        )
    }

    @Test
    fun resolveSpreadPage_outOfOrderLoading_doesNotCombineAcrossSlots() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            BookPage.Spread.Unrated(2),
            BookPage.Spread.Unrated(3),
        )

        // Page 2 and Page 3 load first (portrait)
        val afterPage2 = service.resolvePage(initialList, BookPage.Spread.Unrated(2), true)
        val afterPage3 = service.resolvePage(afterPage2, BookPage.Spread.Unrated(3), true)

        // Pages 2 and 3 should NOT combine into a spread because they belong to different slots
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Unrated(1),
                BookPage.Spread.Single(2),
                BookPage.Spread.Single(3),
            ),
            afterPage3,
        )

        // Delayed Page 1 loads (portrait) -> combines with Page 2
        val afterPage1 = service.resolvePage(afterPage3, BookPage.Spread.Unrated(1), true)
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Combine(1, 2),
                BookPage.Spread.Single(3),
            ),
            afterPage1,
        )
    }

    @Test
    fun resolveSpreadPage_portraitFollowedByLandscape() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            BookPage.Spread.Unrated(2),
            BookPage.Spread.Unrated(3),
            BookPage.Spread.Unrated(4),
        )

        val p0 = service.resolvePage(initialList, BookPage.Spread.Unrated(0), true)
        val p1 = service.resolvePage(p0, BookPage.Spread.Unrated(1), true)
        val p2 = service.resolvePage(p1, BookPage.Spread.Unrated(2), false)
        val p3 = service.resolvePage(p2, BookPage.Spread.Unrated(3), true)
        val p4 = service.resolvePage(p3, BookPage.Spread.Unrated(4), true)

        assertEquals(
            listOf(
                BookPage.Spread.Single(0),
                BookPage.Spread.Single(1),
                BookPage.Spread.Spread2(2),
                BookPage.Spread.Combine(3, 4),
            ),
            p4,
        )
    }

    @Test
    fun resolveSpreadPage_landscapeFollowedByPortraits() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
            BookPage.Spread.Unrated(2),
            BookPage.Spread.Unrated(3),
        )

        val p0 = service.resolvePage(initialList, BookPage.Spread.Unrated(0), true)
        val p1 = service.resolvePage(p0, BookPage.Spread.Unrated(1), false)
        val p2 = service.resolvePage(p1, BookPage.Spread.Unrated(2), true)
        val p3 = service.resolvePage(p2, BookPage.Spread.Unrated(3), true)

        assertEquals(
            listOf(
                BookPage.Spread.Single(0),
                BookPage.Spread.Spread2(1),
                BookPage.Spread.Combine(2, 3),
            ),
            p3,
        )
    }

    @Test
    fun resolveSplitPage_outOfBoundsPage_returnsCurrentListUnchanged() {
        val initialList: List<PageItem> = listOf(
            BookPage.Split.Unrated(0),
            BookPage.Split.Unrated(1),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Split.Unrated(99),
            isPortrait = false,
        )

        assertEquals(initialList, updated)
    }

    @Test
    fun resolveSpreadPage_outOfBoundsPage_returnsCurrentListUnchanged() {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
        )

        val updated = service.resolvePage(
            currentList = initialList,
            unratedPage = BookPage.Spread.Unrated(99),
            isPortrait = true,
        )

        assertEquals(initialList, updated)
        assertTrue(
            service.resolvePage(
                currentList = emptyList(),
                unratedPage = BookPage.Spread.Unrated(0),
                isPortrait = true,
            ).isEmpty(),
        )
    }
}
