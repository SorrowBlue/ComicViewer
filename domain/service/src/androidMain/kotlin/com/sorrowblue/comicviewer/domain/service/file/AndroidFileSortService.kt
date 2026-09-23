/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import java.text.Collator as JavaCollator
import java.util.Locale

@ContributesBinding(AppScope::class)
@Inject
internal class AndroidFileSortService : FileSortService {

    private val collatorThreadLocal = ThreadLocal.withInitial<(String, String) -> Int> {
        try {
            val us = Collator.getInstance(Locale.US) as RuleBasedCollator
            val lo = Collator.getInstance(Locale.getDefault()) as RuleBasedCollator
            val icuCollator = RuleBasedCollator(us.rules + lo.rules).apply {
                strength = Collator.PRIMARY
                numericCollation = true
            }
            val compareFunc: (String, String) -> Int = { a, b -> icuCollator.compare(a, b) }
            compareFunc
        } catch (_: Throwable) {
            val us = JavaCollator.getInstance(Locale.US) as java.text.RuleBasedCollator
            val lo = JavaCollator.getInstance(Locale.getDefault()) as java.text.RuleBasedCollator
            val javaCollator = java.text.RuleBasedCollator(us.rules + lo.rules).apply {
                strength = JavaCollator.PRIMARY
            }
            val compareFunc: (String, String) -> Int = { a, b -> javaCollator.compare(a, b) }
            compareFunc
        }
    }

    override val compareFile: Comparator<File> = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenComparator { a, b -> requireNotNull(collatorThreadLocal.get()).invoke(a.name, b.name) }
}

internal actual fun defaultFileSortService(): FileSortService = AndroidFileSortService()
