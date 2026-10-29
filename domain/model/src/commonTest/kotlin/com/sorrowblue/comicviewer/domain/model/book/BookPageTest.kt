/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.book

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BookPageTest {

    @Test
    fun defaultPage_exposesIndexAndKey() {
        val page = BookPage.Default(index = 3)

        assertEquals(3, page.index)
        assertEquals("Default:3", page.key)
    }

    @Test
    fun spreadPages_exposePropertiesAndFormattedKeys() {
        val single = BookPage.Spread.Single(index = 0)
        assertEquals(0, single.index)
        assertEquals("Spread:single:0", single.key)

        val combine = BookPage.Spread.Combine(index = 1, nextIndex = 2)
        assertEquals(1, combine.index)
        assertEquals(2, combine.nextIndex)
        assertEquals("Spread:combine:1:2", combine.key)

        val spread2 = BookPage.Spread.Spread2(index = 4)
        assertEquals(4, spread2.index)
        assertEquals("Spread:spread2:4", spread2.key)

        val unrated = BookPage.Spread.Unrated(index = 5)
        assertEquals(5, unrated.index)
        assertEquals("Spread:unrated:5", unrated.key)
        assertIs<UnratedPage>(unrated)
    }

    @Test
    fun splitPages_exposePropertiesAndFormattedKeys() {
        val single = BookPage.Split.Single(index = 0)
        assertEquals(0, single.index)
        assertEquals("Split:single:0", single.key)

        val left = BookPage.Split.Left(index = 1)
        assertEquals(1, left.index)
        assertEquals("Split:left:1", left.key)

        val right = BookPage.Split.Right(index = 1)
        assertEquals(1, right.index)
        assertEquals("Split:right:1", right.key)

        val unrated = BookPage.Split.Unrated(index = 2)
        assertEquals(2, unrated.index)
        assertEquals("Split:unrated:2", unrated.key)
        assertIs<UnratedPage>(unrated)
    }

    @Test
    fun nextPage_formatsKeyByNavigationDirection() {
        val next = NextPage(isNext = true, nextBooks = emptyList())
        val prev = NextPage(isNext = false, nextBooks = emptyList())

        assertEquals("next:true", next.key)
        assertEquals("next:false", prev.key)
    }
}
