/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.zip.impl

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.kioarch.ArchiveEntry
import java.util.Locale

private val collator =
    RuleBasedCollator((Collator.getInstance(Locale.US) as RuleBasedCollator).rules).apply {
        strength = Collator.PRIMARY
        numericCollation = true
    }

internal actual fun List<ArchiveEntry>.sortedByName(): List<ArchiveEntry> =
    sortedWith(Comparator.comparing(ArchiveEntry::name, collator::compare))
