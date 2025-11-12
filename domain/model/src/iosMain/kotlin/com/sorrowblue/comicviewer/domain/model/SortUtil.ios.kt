package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import platform.Foundation.NSString
import platform.Foundation.localizedCompare

actual object SortUtil {
    actual val compareFile = compareBy<File> { if (it is BookFile) 1 else 0 }
        .thenComparator { a, b ->
            @Suppress("CAST_NEVER_SUCCEEDS")
            val str = a.name as NSString
            str.localizedCompare(b.name).toInt()
        }

    actual fun filter(file: File, supportExtensions: List<String>): Boolean =
        file is IFolder || (file is BookFile && file.extension in supportExtensions)

    actual fun sortedIndex(list: List<File>): List<File> = list
        .sortedWith(compareFile)
        .mapIndexed { index, fileModel ->
            when (fileModel) {
                is BookFile -> fileModel.copy(sortIndex = index)
                is Folder -> fileModel.copy(sortIndex = index)
                is BookFolder -> fileModel.copy(sortIndex = index)
            }
        }
}
