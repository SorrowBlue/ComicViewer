/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder

/**
 * Domain service for natural-order file sorting, extension filtering, and sort index assignment.
 */
interface FileSortService {

    /**
     * Comparator for comparing files (folders first, followed by natural order sorting).
     */
    val compareFile: Comparator<File>

    /**
     * Filters a file based on the specified list of supported extensions.
     *
     * @param file Target file to evaluate.
     * @param supportExtensions List of supported file extensions.
     * @return `true` if the file should be retained.
     */
    fun filter(file: File, supportExtensions: List<String>): Boolean =
        file is IFolder || (file is BookFile && file.extension in supportExtensions)

    /**
     * Sorts a list of files in natural order and returns a new list with sequential [File.sortIndex] values assigned.
     *
     * @param list List of files to sort.
     * @return Sorted list of files with updated sort indices.
     */
    fun sortedIndex(list: List<File>): List<File> = list
        .sortedWith(compareFile)
        .mapIndexed { index, fileModel ->
            when (fileModel) {
                is BookFile -> fileModel.copy(sortIndex = index)
                is Folder -> fileModel.copy(sortIndex = index)
                is BookFolder -> fileModel.copy(sortIndex = index)
            }
        }

    companion object {
        /**
         * Returns the default [FileSortService] implementation for the current platform.
         */
        operator fun invoke(): FileSortService = defaultFileSortService()
    }
}

internal expect fun defaultFileSortService(): FileSortService

internal fun compareNatural(a: String, b: String, compareText: (String, String) -> Int): Int {
    var indexA = 0
    var indexB = 0
    var zeroLengthTieBreaker = 0
    while (indexA < a.length && indexB < b.length) {
        val endA = findChunkEnd(a, indexA)
        val endB = findChunkEnd(b, indexB)
        val chunkA = a.substring(indexA, endA)
        val chunkB = b.substring(indexB, endB)
        val cmp = if (a[indexA].isAsciiDigit() && b[indexB].isAsciiDigit()) {
            compareNumericChunks(chunkA, chunkB).also {
                if (it == 0 && zeroLengthTieBreaker == 0) {
                    zeroLengthTieBreaker = chunkA.length.compareTo(chunkB.length)
                }
            }
        } else {
            compareText(chunkA, chunkB)
        }
        if (cmp != 0) return cmp
        indexA = endA
        indexB = endB
    }
    val remainingCmp = (a.length - indexA).compareTo(b.length - indexB)
    return if (remainingCmp != 0) remainingCmp else zeroLengthTieBreaker
}

private fun findChunkEnd(text: String, start: Int): Int {
    val isDigitChunk = text[start].isAsciiDigit()
    var index = start + 1
    while (index < text.length && text[index].isAsciiDigit() == isDigitChunk) {
        index++
    }
    return index
}

private fun compareNumericChunks(chunkA: String, chunkB: String): Int {
    val trimmedA = chunkA.trimStart('0')
    val trimmedB = chunkB.trimStart('0')
    val lengthCmp = trimmedA.length.compareTo(trimmedB.length)
    return if (lengthCmp != 0) lengthCmp else trimmedA.compareTo(trimmedB)
}

private fun Char.isAsciiDigit(): Boolean = this in '0'..'9'
