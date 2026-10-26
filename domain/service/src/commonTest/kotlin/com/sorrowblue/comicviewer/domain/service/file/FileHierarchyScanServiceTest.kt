/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class FileHierarchyScanServiceTest {

    private val fileSortService = FileSortService()
    private val service = FileHierarchyScanServiceImpl(fileSortService)
    private val bookshelfId = BookshelfId(1)
    private val supportExtensions = listOf("zip", "cbz")

    @Test
    fun testScanHierarchy_deepTraversalAndFiltering() = runTest {
        val root = Folder(bookshelfId, "root", "", "/root", 0, 0, false)
        val subFolder = Folder(bookshelfId, "sub", "/root", "/root/sub", 0, 0, false)
        val book2 = BookFile(bookshelfId, "02.zip", "/root", "/root/02.zip", 100, 0, false)
        val book1 = BookFile(bookshelfId, "01.cbz", "/root", "/root/01.cbz", 100, 0, false)
        val txtFile = BookFile(bookshelfId, "readme.txt", "/root", "/root/readme.txt", 10, 0, false)

        val subBook = BookFile(
            bookshelfId,
            "sub_01.zip",
            "/root/sub",
            "/root/sub/sub_01.zip",
            50,
            0,
            false,
        )

        val filesMap = mapOf(
            root.path to listOf(book2, txtFile, subFolder, book1),
            subFolder.path to listOf(subBook),
        )

        val scannedFolders = mutableListOf<Pair<File, List<File>>>()
        val discoveredFiles = mutableListOf<File>()

        service.scanHierarchy(
            root = root,
            supportExtensions = supportExtensions,
            listFiles = { file, filter ->
                filesMap[file.path]?.filter(filter).orEmpty()
            },
            onFolderFilesScanned = { parent, files ->
                scannedFolders.add(parent to files)
            },
            onFileDiscovered = { file ->
                discoveredFiles.add(file)
            },
        )

        // フォルダごとの走査結果を検証
        assertEquals(2, scannedFolders.size)

        // root直下: subFolder(フォルダ優先) -> 01.cbz -> 02.zip の順 (readme.txt は除外)
        val (rootParent, rootFiles) = scannedFolders[0]
        assertEquals(root.path, rootParent.path)
        assertEquals(listOf("sub", "01.cbz", "02.zip"), rootFiles.map { it.name })
        assertEquals(listOf(0, 1, 2), rootFiles.map { it.sortIndex })

        // subFolder直下: sub_01.zip
        val (subParent, subFiles) = scannedFolders[1]
        assertEquals(subFolder.path, subParent.path)
        assertEquals(listOf("sub_01.zip"), subFiles.map { it.name })
        assertEquals(listOf(0), subFiles.map { it.sortIndex })

        // 全体の発見ファイル順序 (DFS順)
        assertEquals(
            listOf("sub", "01.cbz", "02.zip", "sub_01.zip"),
            discoveredFiles.map { it.name },
        )
    }

    @Test
    fun testScanHierarchy_emptyFolder() = runTest {
        val root = Folder(bookshelfId, "root", "", "/root", 0, 0, false)
        val scannedFolders = mutableListOf<Pair<File, List<File>>>()
        val discoveredFiles = mutableListOf<File>()

        service.scanHierarchy(
            root = root,
            supportExtensions = supportExtensions,
            listFiles = { _, _ -> emptyList() },
            onFolderFilesScanned = { parent, files ->
                scannedFolders.add(parent to files)
            },
            onFileDiscovered = { file ->
                discoveredFiles.add(file)
            },
        )

        assertEquals(1, scannedFolders.size)
        assertEquals(root.path, scannedFolders[0].first.path)
        assertTrue(scannedFolders[0].second.isEmpty())
        assertTrue(discoveredFiles.isEmpty())
    }

    @Test
    fun testScanHierarchy_multipleSiblingSubfolders_preservesPreOrderDfs() = runTest {
        val root = Folder(bookshelfId, "root", "", "/root", 0, 0, false)
        val sub1 = Folder(bookshelfId, "sub1", "/root", "/root/sub1", 0, 0, false)
        val sub2 = Folder(bookshelfId, "sub2", "/root", "/root/sub2", 0, 0, false)
        val sub1Book = BookFile(
            bookshelfId,
            "sub1_01.zip",
            "/root/sub1",
            "/root/sub1/sub1_01.zip",
            50,
            0,
            false,
        )
        val sub2Book = BookFile(
            bookshelfId,
            "sub2_01.zip",
            "/root/sub2",
            "/root/sub2/sub2_01.zip",
            50,
            0,
            false,
        )

        val filesMap = mapOf(
            root.path to listOf(sub2, sub1),
            sub1.path to listOf(sub1Book),
            sub2.path to listOf(sub2Book),
        )

        val scannedFolders = mutableListOf<Pair<File, List<File>>>()
        val discoveredFiles = mutableListOf<File>()

        service.scanHierarchy(
            root = root,
            supportExtensions = supportExtensions,
            listFiles = { file, filter ->
                filesMap[file.path]?.filter(filter).orEmpty()
            },
            onFolderFilesScanned = { parent, files ->
                scannedFolders.add(parent to files)
            },
            onFileDiscovered = { file ->
                discoveredFiles.add(file)
            },
        )

        assertEquals(
            listOf("/root", "/root/sub1", "/root/sub2"),
            scannedFolders.map { it.first.path },
        )
        assertEquals(
            listOf("sub1", "sub2", "sub1_01.zip", "sub2_01.zip"),
            discoveredFiles.map { it.name },
        )
    }
}
