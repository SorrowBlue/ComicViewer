package com.sorrowblue.comicviewer.domain.model

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import java.util.Locale

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object SortUtil {

    private val collator: Collator
        get() {
            val us = Collator.getInstance(Locale.US) as RuleBasedCollator
            val lo = Collator.getInstance(Locale.getDefault()) as RuleBasedCollator
            return RuleBasedCollator(us.rules + lo.rules).apply {
                strength = Collator.PRIMARY
                numericCollation = true
            }
        }

    actual val compareFile = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenBy({ a, b -> collator.compare(a, b) }, { it.name })

    actual fun filter(file: File, supportExtensions: List<String>): Boolean {
        return file is IFolder || (file is BookFile && file.extension in supportExtensions)
    }

    actual fun sortedIndex(list: List<File>): List<File> {
        return list.sortedWith(compareFile)
            .mapIndexed { index, fileModel ->
                when (fileModel) {
                    is BookFile -> fileModel.copy(sortIndex = index)
                    is Folder -> fileModel.copy(sortIndex = index)
                    is BookFolder -> fileModel.copy(sortIndex = index)
                }
            }
    }
}
