/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

@OptIn(InternalDataApi::class)
class FileThumbnailTest {

    private fun createBookFile(
        bookshelfId: BookshelfId = BookshelfId(1),
        name: String = "book.cbz",
        parent: String = "/books",
        path: String = "/books/book.cbz",
        size: Long = 1024L,
        lastModifier: Long = 1000L,
        isHidden: Boolean = false,
        cacheKey: String = "cache-1",
        lastPageRead: Int = 0,
        totalPageCount: Int = 20,
        lastReadTime: Long = 0L,
        sortIndex: Int = 0,
    ) = BookFile(
        bookshelfId = bookshelfId,
        name = name,
        parent = parent,
        path = path,
        size = size,
        lastModifier = lastModifier,
        isHidden = isHidden,
        cacheKey = cacheKey,
        lastPageRead = lastPageRead,
        totalPageCount = totalPageCount,
        lastReadTime = lastReadTime,
        sortIndex = sortIndex,
    )

    private fun createFolder(
        bookshelfId: BookshelfId = BookshelfId(1),
        name: String = "folder",
        parent: String = "/",
        path: String = "/folder",
        size: Long = 0L,
        lastModifier: Long = 2000L,
        isHidden: Boolean = false,
        sortIndex: Int = 0,
        cacheKey: String = "folder-cache-1",
        count: Int = 5,
    ) = Folder(
        bookshelfId = bookshelfId,
        name = name,
        parent = parent,
        path = path,
        size = size,
        lastModifier = lastModifier,
        isHidden = isHidden,
        sortIndex = sortIndex,
        cacheKey = cacheKey,
        count = count,
    )

    @Test
    fun bookThumbnailFrom_copiesRelevantMetadataAndSupportsEquality() {
        val book1 = createBookFile(lastPageRead = 2, cacheKey = "key-a")
        val book2 = createBookFile(lastPageRead = 15, cacheKey = "key-b")

        val thumbnail1 = BookThumbnail.from(book1)
        val thumbnail2 = BookThumbnail.from(book2)

        assertEquals(book1.bookshelfId, thumbnail1.bookshelfId)
        assertEquals(book1.path, thumbnail1.path)
        assertEquals(book1.lastModifier, thumbnail1.lastModifier)
        assertEquals(book1.size, thumbnail1.size)
        assertEquals(book1.totalPageCount, thumbnail1.totalPageCount)

        assertEquals(thumbnail1, thumbnail2)
        assertEquals(thumbnail1.hashCode(), thumbnail2.hashCode())

        val differentBookshelf = BookThumbnail.from(createBookFile(bookshelfId = BookshelfId(2)))
        val differentPath = BookThumbnail.from(createBookFile(path = "/books/other.cbz"))
        val differentModified = BookThumbnail.from(createBookFile(lastModifier = 9999L))

        assertNotEquals(thumbnail1, differentBookshelf)
        assertNotEquals(thumbnail1, differentPath)
        assertNotEquals(thumbnail1, differentModified)
    }

    @Test
    fun folderThumbnailFrom_copiesRelevantMetadataAndSupportsEquality() {
        val folder1 = createFolder(count = 3, cacheKey = "key-1")
        val folder2 = createFolder(count = 10, cacheKey = "key-2")

        val thumbnail1 = FolderThumbnail.from(folder1)
        val thumbnail2 = FolderThumbnail.from(folder2)

        assertEquals(folder1.bookshelfId, thumbnail1.bookshelfId)
        assertEquals(folder1.path, thumbnail1.path)
        assertEquals(folder1.lastModifier, thumbnail1.lastModifier)
        assertEquals(folder1.size, thumbnail1.size)

        assertEquals(thumbnail1, thumbnail2)
        assertEquals(thumbnail1.hashCode(), thumbnail2.hashCode())

        val differentBookshelf = FolderThumbnail.from(createFolder(bookshelfId = BookshelfId(2)))
        val differentPath = FolderThumbnail.from(createFolder(path = "/other"))
        val differentModified = FolderThumbnail.from(createFolder(lastModifier = 9999L))

        assertNotEquals(thumbnail1, differentBookshelf)
        assertNotEquals(thumbnail1, differentPath)
        assertNotEquals(thumbnail1, differentModified)
    }

    @Test
    fun fileThumbnailFrom_dispatchesToBookOrFolderThumbnail() {
        val book = createBookFile()
        val folder = createFolder()

        val bookThumbnail = FileThumbnail.from(book)
        val folderThumbnail = FileThumbnail.from(folder)

        assertIs<BookThumbnail>(bookThumbnail)
        assertEquals(BookThumbnail.from(book), bookThumbnail)

        assertIs<FolderThumbnail>(folderThumbnail)
        assertEquals(FolderThumbnail.from(folder), folderThumbnail)
    }
}
