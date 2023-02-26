package com.sorrowblue.comicviewer.data.common.util

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.data.common.FileModel
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

    fun filter(fileModel: FileModel, supportExtensions: List<String>): Boolean {
        return fileModel is FileModel.Folder || fileModel is FileModel.ImageFolder || fileModel.extension in supportExtensions
    }

    private val sort = compareBy<FileModel> { if (it is FileModel.File) 1 else 0 }
        .thenBy(collator::compare, FileModel::name)


    fun sortedIndex(list: List<FileModel>): List<FileModel> {
        return list.sortedWith(sort)
            .mapIndexed { index, fileModel ->
                when (fileModel) {
                    is FileModel.File -> fileModel.copy(sortIndex = index)
                    is FileModel.Folder -> fileModel.copy(sortIndex = index)
                    is FileModel.ImageFolder -> fileModel.copy(sortIndex = index)
                }
            }
    }

}
