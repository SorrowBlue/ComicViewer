/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import kotlin.test.Test
import kotlin.test.assertEquals

class PageItemExtensionTest {

    @Test
    fun toDisplayPage_returnsExpectedPage() {
        val totalPages = 100
        assertEquals(0, NextPage(isNext = false, emptyList()).toDisplayPage(totalPages))
        assertEquals(101, NextPage(isNext = true, emptyList()).toDisplayPage(totalPages))

        assertEquals(1, BookPage.Default(index = 0).toDisplayPage(totalPages))
        assertEquals(50, BookPage.Default(index = 49).toDisplayPage(totalPages))

        assertEquals(1, BookPage.Spread.Single(index = 0).toDisplayPage(totalPages))
        assertEquals(3, BookPage.Spread.Combine(index = 1, nextIndex = 2).toDisplayPage(totalPages))
        assertEquals(4, BookPage.Spread.Spread2(index = 3).toDisplayPage(totalPages))
        assertEquals(5, BookPage.Spread.Unrated(index = 4).toDisplayPage(totalPages))

        assertEquals(6, BookPage.Split.Right(index = 5).toDisplayPage(totalPages))
        assertEquals(6, BookPage.Split.Left(index = 5).toDisplayPage(totalPages))
        assertEquals(7, BookPage.Split.Single(index = 6).toDisplayPage(totalPages))
        assertEquals(8, BookPage.Split.Unrated(index = 7).toDisplayPage(totalPages))
    }

    @Test
    fun toLastReadPage_returnsClampedIndex() {
        val totalPages = 100
        assertEquals(0, NextPage(isNext = false, emptyList()).toLastReadPage(totalPages))
        assertEquals(99, NextPage(isNext = true, emptyList()).toLastReadPage(totalPages))

        assertEquals(0, BookPage.Default(index = 0).toLastReadPage(totalPages))
        assertEquals(49, BookPage.Default(index = 49).toLastReadPage(totalPages))

        assertEquals(0, BookPage.Spread.Single(index = 0).toLastReadPage(totalPages))
        assertEquals(
            2,
            BookPage.Spread.Combine(index = 1, nextIndex = 2).toLastReadPage(totalPages),
        )
        assertEquals(
            99,
            BookPage.Spread.Combine(index = 99, nextIndex = 100).toLastReadPage(totalPages),
        )

        assertEquals(5, BookPage.Split.Right(index = 5).toLastReadPage(totalPages))
        assertEquals(5, BookPage.Split.Left(index = 5).toLastReadPage(totalPages))
    }

    @Test
    fun findPagerIndex_emptyList_returnsZero() {
        val list = emptyList<PageItem>()
        assertEquals(0, list.findPagerIndex(1, 10))
    }

    @Test
    fun findPagerIndex_prevAndNextBookBounds() {
        val list = listOf(
            NextPage(isNext = false, emptyList()),
            BookPage.Default(0),
            BookPage.Default(1),
            NextPage(isNext = true, emptyList()),
        )
        val totalPages = 2
        assertEquals(0, list.findPagerIndex(0, totalPages))
        assertEquals(0, list.findPagerIndex(-1, totalPages))
        assertEquals(3, list.findPagerIndex(3, totalPages))
        assertEquals(3, list.findPagerIndex(99, totalPages))
    }

    @Test
    fun findPagerIndex_defaultFormat_returnsExactIndex() {
        val list = listOf(
            NextPage(isNext = false, emptyList()),
            BookPage.Default(0),
            BookPage.Default(1),
            BookPage.Default(2),
            NextPage(isNext = true, emptyList()),
        )
        val totalPages = 3
        assertEquals(1, list.findPagerIndex(1, totalPages))
        assertEquals(2, list.findPagerIndex(2, totalPages))
        assertEquals(3, list.findPagerIndex(3, totalPages))
    }

    @Test
    fun findPagerIndex_spreadFormat_returnsSpreadIndex() {
        val list = listOf(
            NextPage(isNext = false, emptyList()),
            BookPage.Spread.Single(0),
            BookPage.Spread.Combine(1, 2),
            BookPage.Spread.Combine(3, 4),
            NextPage(isNext = true, emptyList()),
        )
        val totalPages = 5
        // Page 1 is Single(0) at index 1
        assertEquals(1, list.findPagerIndex(1, totalPages))
        // Pages 2 and 3 are Combine(1, 2) at index 2
        assertEquals(2, list.findPagerIndex(2, totalPages))
        assertEquals(2, list.findPagerIndex(3, totalPages))
        // Pages 4 and 5 are Combine(3, 4) at index 3
        assertEquals(3, list.findPagerIndex(4, totalPages))
        assertEquals(3, list.findPagerIndex(5, totalPages))
    }

    @Test
    fun findPagerIndex_splitFormat_returnsFirstPart() {
        val list = listOf(
            NextPage(isNext = false, emptyList()),
            BookPage.Split.Single(0),
            BookPage.Split.Right(1),
            BookPage.Split.Left(1),
            NextPage(isNext = true, emptyList()),
        )
        val totalPages = 2
        assertEquals(1, list.findPagerIndex(1, totalPages))
        // Page 2 is split into Right(1) and Left(1). Searching page 2 finds the first half (Right(1)) at index 2
        assertEquals(2, list.findPagerIndex(2, totalPages))
    }

    @Test
    fun findPagerIndexFor_splitLeft_retainsLeftPosition() {
        val list = listOf(
            BookPage.Split.Single(0),
            BookPage.Split.Right(1),
            BookPage.Split.Left(1),
            BookPage.Split.Single(2),
        )
        val totalPages = 3
        val currentItem = BookPage.Split.Left(1)
        // Even though targetDisplayPage is 2 (which findPagerIndex would resolve to index 1/Right),
        // findPagerIndexFor must retain the exact position of Left(1) at index 2.
        assertEquals(
            2,
            list.findPagerIndexFor(
                currentItem = currentItem,
                targetDisplayPage = 2,
                totalPageCount = totalPages,
            ),
        )
    }

    @Test
    fun findPagerIndexFor_splitRight_retainsRightPosition() {
        val list = listOf(
            BookPage.Split.Single(0),
            BookPage.Split.Right(1),
            BookPage.Split.Left(1),
            BookPage.Split.Single(2),
        )
        val totalPages = 3
        val currentItem = BookPage.Split.Right(1)
        assertEquals(
            1,
            list.findPagerIndexFor(
                currentItem = currentItem,
                targetDisplayPage = 2,
                totalPageCount = totalPages,
            ),
        )
    }

    @Test
    fun findPagerIndexFor_splitUnrated_resolvesToRightFirst() {
        val list = listOf(
            BookPage.Split.Single(0),
            BookPage.Split.Right(1),
            BookPage.Split.Left(1),
            BookPage.Split.Unrated(2),
        )
        val totalPages = 3
        val currentItem = BookPage.Split.Unrated(1)
        // When resolving an unrated split page, right page should be shown first (RTL reading)
        assertEquals(
            1,
            list.findPagerIndexFor(
                currentItem = currentItem,
                targetDisplayPage = 2,
                totalPageCount = totalPages,
            ),
        )
    }

    @Test
    fun findPagerIndexFor_nullCurrentItem_fallsBackToDisplayPage() {
        val list = listOf(
            BookPage.Split.Single(0),
            BookPage.Split.Right(1),
            BookPage.Split.Left(1),
        )
        val totalPages = 2
        assertEquals(
            1,
            list.findPagerIndexFor(
                currentItem = null,
                targetDisplayPage = 2,
                totalPageCount = totalPages,
            ),
        )
    }
}
