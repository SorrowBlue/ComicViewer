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
 * ファイルの自然順ソート、拡張子フィルタリング、およびソートインデックスの採番を行うドメインサービス。
 */
interface FileSortService {

    /**
     * ファイルを比較するためのコンパレータ（フォルダー優先、自然順ソート）。
     */
    val compareFile: Comparator<File>

    /**
     * 指定された拡張子リストに基づいてファイルをフィルタリングします。
     *
     * @param file 判定対象のファイル
     * @param supportExtensions サポートされている拡張子のリスト
     * @return 保持する場合は true
     */
    fun filter(file: File, supportExtensions: List<String>): Boolean =
        file is IFolder || (file is BookFile && file.extension in supportExtensions)

    /**
     * ファイル一覧を自然順でソートし、[File.sortIndex] を連番で付与した新しいリストを返します。
     *
     * @param list ソート対象のファイル一覧
     * @return ソートおよびインデックスが付与されたファイル一覧
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

    /**
     * ファイル一覧を自然順でソートします。
     *
     * @param list ソート対象のファイル一覧
     * @return ソートされたファイル一覧
     */
    fun sort(list: List<File>): List<File> = list.sortedWith(compareFile)

    companion object {
        /**
         * 現在のプラットフォームに対応したデフォルトの [FileSortService] 実装を返します。
         */
        operator fun invoke(): FileSortService = defaultFileSortService()
    }
}

internal expect fun defaultFileSortService(): FileSortService
