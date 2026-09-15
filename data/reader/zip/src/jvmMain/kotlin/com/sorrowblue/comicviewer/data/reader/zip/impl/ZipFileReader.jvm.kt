/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.kioarch.ArchiveEntry
import java.text.Collator
import java.text.RuleBasedCollator
import java.util.Locale

private val collator =
    RuleBasedCollator((Collator.getInstance(Locale.US) as RuleBasedCollator).rules).apply {
        strength = Collator.PRIMARY
    }

internal actual fun List<ArchiveEntry>.sortedByName(): List<ArchiveEntry> =
    sortedWith(Comparator.comparing(ArchiveEntry::name, collator::compare))
