/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.kioarch.ArchiveEntry
import platform.Foundation.NSString
import platform.Foundation.localizedStandardCompare

internal actual fun List<ArchiveEntry>.sortedByName(): List<ArchiveEntry> {
    return sortedWith(Comparator { a, b ->
        @Suppress("CAST_NEVER_SUCCEEDS")
        (a.name as NSString).localizedStandardCompare(b.name).toInt()
    })
}
