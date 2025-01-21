package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object SortUtil {
    actual val compareFile: Comparator<File>
        get() = Comparator<File> { a, b -> TODO("Not yet implemented") }

    actual fun filter(
        file: File,
        supportExtensions: List<String>,
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual fun sortedIndex(list: List<File>): List<File> {
        TODO("Not yet implemented")
    }
}
