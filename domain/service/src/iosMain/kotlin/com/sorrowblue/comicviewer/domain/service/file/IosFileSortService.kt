/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import platform.Foundation.NSString
import platform.Foundation.localizedCompare

@ContributesBinding(AppScope::class)
@Inject
internal class IosFileSortService : FileSortService {

    override val compareFile: Comparator<File> = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenComparator { a, b ->
            @Suppress("CAST_NEVER_SUCCEEDS")
            val str = a.name as NSString
            str.localizedCompare(b.name).toInt()
        }
}

internal actual fun defaultFileSortService(): FileSortService = IosFileSortService()
