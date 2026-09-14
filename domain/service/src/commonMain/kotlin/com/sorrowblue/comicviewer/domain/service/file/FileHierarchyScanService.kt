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
 * ファイル階層の再帰的走査、フィルタリング、ソートおよびインデックス採番を行うドメインサービス。
 */
interface FileHierarchyScanService {

    /**
     * 指定されたルート要素からファイル階層を再帰的に走査（深さ優先探索）します。
     *
     * @param root 走査開始ノード（通常はルートフォルダー）
     * @param resolveImageFolder 画像フォルダーをブックとして解決するかどうか
     * @param supportExtensions サポートするファイル拡張子のリスト
     * @param listFiles 指定ノード直下の未ソートファイル一覧を取得する処理（フィルタ条件付き）
     * @param onFolderFilesScanned 親フォルダーと、自然順ソート・採番が完了した直下ファイル一覧が確定した際に呼ばれるコールバック
     * @param onFileDiscovered 発見されたファイルごとに呼ばれるコールバック
     */
    suspend fun scanHierarchy(
        root: File,
        resolveImageFolder: Boolean,
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
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
        listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
        onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
        onFileDiscovered: suspend (file: File) -> Unit,
    ) {
        val fileModelList = fileSortService.sortedIndex(
            listFiles(root) {
                fileSortService.filter(it, supportExtensions)
            },
        )
        onFolderFilesScanned(root, fileModelList)
        fileModelList.forEach {
            onFileDiscovered(it)
        }
        fileModelList
            .filterIsInstance<IFolder>()
            .forEach { folder ->
                scanHierarchy(
                    root = folder,
                    resolveImageFolder = resolveImageFolder,
                    supportExtensions = supportExtensions,
                    listFiles = listFiles,
                    onFolderFilesScanned = onFolderFilesScanned,
                    onFileDiscovered = onFileDiscovered,
                )
            }
    }
}
