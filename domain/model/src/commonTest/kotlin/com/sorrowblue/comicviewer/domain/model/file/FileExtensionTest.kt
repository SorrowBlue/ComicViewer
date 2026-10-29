/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import kotlin.test.Test
import kotlin.test.assertEquals

class FileExtensionTest {

    @Test
    fun extension_extractsLowercaseExtensionAfterLastDot() {
        assertEquals("cbz", "archive.cbz".extension)
        assertEquals("zip", "ARCHIVE.ZIP".extension)
        assertEquals("gz", "a.b.tar.gz".extension)
        assertEquals("pdf", "/path/to/my.folder/Book.PDF".extension)
    }

    @Test
    fun extension_returnsEmptyStringWhenNoDotOrTrailingDot() {
        assertEquals("", "noext".extension)
        assertEquals("", "".extension)
        assertEquals("", "file.".extension)
    }

    @Test
    fun extension_handlesLeadingDotFileNames() {
        assertEquals("hidden", ".hidden".extension)
        assertEquals("cbz", ".hidden.cbz".extension)
    }
}
