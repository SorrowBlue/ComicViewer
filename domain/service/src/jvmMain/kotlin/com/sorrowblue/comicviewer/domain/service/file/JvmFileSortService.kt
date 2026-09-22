/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import java.text.Collator
import java.text.RuleBasedCollator
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
internal class JvmFileSortService : FileSortService {

    private val collatorThreadLocal: ThreadLocal<Collator> = ThreadLocal.withInitial {
        val us = Collator.getInstance(Locale.US) as RuleBasedCollator
        val lo = Collator.getInstance(Locale.getDefault()) as RuleBasedCollator
        RuleBasedCollator(us.rules + lo.rules).apply {
            strength = Collator.PRIMARY
        }
    }

    override val compareFile: Comparator<File> = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenComparator { a, b -> requireNotNull(collatorThreadLocal.get()).compare(a.name, b.name) }
}

internal actual fun defaultFileSortService(): FileSortService = JvmFileSortService()
