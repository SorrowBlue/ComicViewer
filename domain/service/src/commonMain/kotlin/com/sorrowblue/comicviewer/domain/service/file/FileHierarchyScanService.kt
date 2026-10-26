/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Domain service that performs traversal, filtering, sorting, and index assignment across a file hierarchy.
 */
interface FileHierarchyScanService {

    /**
     * Traverses the file hierarchy in depth-first pre-order starting from the specified root element.
     *
     * @param root Root node to start scanning from (typically the root folder).
     * @param supportExtensions List of supported file extensions.
     * @param listFiles Function to retrieve the unsorted list of direct children under a node with a filter predicate.
     * @param onFolderFilesScanned Callback invoked when a parent folder and its naturally sorted, indexed children are finalized.
     * @param onFileDiscovered Callback invoked for each discovered file.
     */
    suspend fun scanHierarchy(
        root: File,
        supportExtensions: List<String>,
        listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
        onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
        onFileDiscovered: suspend (file: File) -> Unit,
    )
}

@ContributesBinding(AppScope::class)
@Inject
class FileHierarchyScanServiceImpl(private val fileSortService: FileSortService) :
    FileHierarchyScanService {

    override suspend fun scanHierarchy(
        root: File,
        supportExtensions: List<String>,
        listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
        onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
        onFileDiscovered: suspend (file: File) -> Unit,
    ) {
        val stack = ArrayDeque<File>()
        stack.addLast(root)
        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            val fileModelList = fileSortService.sortedIndex(
                listFiles(current) {
                    fileSortService.filter(it, supportExtensions)
                },
            )
            onFolderFilesScanned(current, fileModelList)
            fileModelList.forEach {
                onFileDiscovered(it)
            }
            fileModelList
                .filterIsInstance<IFolder>()
                .asReversed()
                .forEach { folder ->
                    stack.addLast(folder)
                }
        }
    }
}
