/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(InternalDataApi::class)
class FileSortServiceTest {

    private val service = FileSortService()
    private val bookshelfId = BookshelfId(1)

    @Test
    fun testFilter() {
        val folder = Folder(bookshelfId, "folder", "", "/folder", 0, 0, false)
        val zipBook = BookFile(bookshelfId, "book.zip", "", "/book.zip", 100, 0, false)
        val txtFile = BookFile(bookshelfId, "readme.txt", "", "/readme.txt", 10, 0, false)

        val supportExtensions = listOf("zip", "cbz")

        assertTrue(service.filter(folder, supportExtensions))
        assertTrue(service.filter(zipBook, supportExtensions))
        assertFalse(service.filter(txtFile, supportExtensions))
    }

    @Test
    fun testSortFoldersBeforeFiles() {
        val folder = Folder(bookshelfId, "A_Folder", "", "/A_Folder", 0, 0, false)
        val file = BookFile(bookshelfId, "0_Book.zip", "", "/0_Book.zip", 100, 0, false)

        val list = listOf(file, folder)
        val sorted = service.sortedIndex(list)

        assertEquals(folder.name, sorted[0].name)
        assertEquals(0, sorted[0].sortIndex)
        assertEquals(file.name, sorted[1].name)
        assertEquals(1, sorted[1].sortIndex)
    }

    @Test
    fun testSortedIndexSequential() {
        val file1 = BookFile(bookshelfId, "01.zip", "", "/01.zip", 100, 0, false)
        val file2 = BookFile(bookshelfId, "02.zip", "", "/02.zip", 100, 0, false)
        val file3 = BookFile(bookshelfId, "03.zip", "", "/03.zip", 100, 0, false)

        val list = listOf(file3, file1, file2)
        val sorted = service.sortedIndex(list)

        assertEquals(listOf("01.zip", "02.zip", "03.zip"), sorted.map { it.name })
        assertEquals(listOf(0, 1, 2), sorted.map { it.sortIndex })
    }

    @Test
    fun testNaturalNumericSort() {
        val file1 = BookFile(bookshelfId, "vol1.zip", "", "/vol1.zip", 100, 0, false)
        val file2 = BookFile(bookshelfId, "vol2.zip", "", "/vol2.zip", 100, 0, false)
        val file10 = BookFile(bookshelfId, "vol10.zip", "", "/vol10.zip", 100, 0, false)

        val list = listOf(file10, file2, file1)
        val sorted = service.sortedIndex(list)

        assertEquals(listOf("vol1.zip", "vol2.zip", "vol10.zip"), sorted.map { it.name })
        assertEquals(listOf(0, 1, 2), sorted.map { it.sortIndex })
    }

    @Test
    fun testNaturalSortLeadingZeroAndCaseTieBreaker() {
        val file1 = BookFile(bookshelfId, "vol1.zip", "", "/vol1.zip", 100, 0, false)
        val file01 = BookFile(bookshelfId, "vol01.zip", "", "/vol01.zip", 100, 0, false)
        val fileUpper2 = BookFile(bookshelfId, "Vol2.zip", "", "/Vol2.zip", 100, 0, false)
        val fileLower10 = BookFile(bookshelfId, "vol10.zip", "", "/vol10.zip", 100, 0, false)

        val sorted = service.sortedIndex(listOf(fileLower10, file01, fileUpper2, file1))

        assertEquals(
            listOf("vol1.zip", "vol01.zip", "Vol2.zip", "vol10.zip"),
            sorted.map { it.name },
        )
        assertEquals(listOf(0, 1, 2, 3), sorted.map { it.sortIndex })
    }
}
