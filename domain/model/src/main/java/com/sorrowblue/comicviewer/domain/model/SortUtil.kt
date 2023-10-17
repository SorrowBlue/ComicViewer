package com.sorrowblue.comicviewer.domain.model

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import java.util.Locale

object SortUtil {

    private val collator: Collator
        get() {
            val us = Collator.getInstance(Locale.US) as RuleBasedCollator
            val lo = Collator.getInstance(Locale.getDefault()) as RuleBasedCollator
            return RuleBasedCollator(us.rules + lo.rules).apply {
                strength = Collator.PRIMARY
                numericCollation = true
            }
        }

    fun filter(file: File, supportExtensions: List<String>): Boolean {
        return file is IFolder || (file is BookFile && file.extension in supportExtensions)
    }

    private val sort = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenBy(collator::compare, File::name)


    fun sortedIndex(list: List<File>): List<File> {
        return list.sortedWith(sort)
            .mapIndexed { index, fileModel ->
                when (fileModel) {
                    is BookFile -> fileModel.copy(sortIndex = index)
                    is Folder -> fileModel.copy(sortIndex = index)
                    is BookFolder -> fileModel.copy(sortIndex = index)
                }
            }
    }

}

val SUPPORTED_IMAGE = setOf(
    "bmp", "gif", "jpg", "jpeg", "png", "webp", "heic", "heif"
)
