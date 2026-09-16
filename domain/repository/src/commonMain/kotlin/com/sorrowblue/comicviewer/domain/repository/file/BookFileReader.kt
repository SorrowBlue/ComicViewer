/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository.file

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * 本ファイルの内容（ページデータやページ数）を読み取るリーダーポート。
 */
interface BookFileReader : AutoCloseable {

    /**
     * 総ページ数を取得する。
     *
     * @return ページ数
     */
    suspend fun pageCount(): Int

    /**
     * 指定されたページのデータソースを取得する。
     *
     * @param pageIndex ページ番号 (0-indexed)
     * @return ページデータの [Source]
     */
    suspend fun source(pageIndex: Int): Source

    /**
     * 指定されたページのデータを指定の [Sink] に展開する。
     *
     * @param pageIndex ページ番号 (0-indexed)
     * @param sink 出力先 [Sink]
     */
    suspend fun extractTo(pageIndex: Int, sink: Sink)

    /**
     * 指定されたページのファイルサイズ（バイト数）を取得する。
     *
     * @param pageIndex ページ番号 (0-indexed)
     * @return ファイルサイズ
     */
    suspend fun fileSize(pageIndex: Int): Long

    /**
     * 指定されたページのエントリファイル名を取得する。
     *
     * @param pageIndex ページ番号 (0-indexed)
     * @return ファイル名
     */
    suspend fun fileName(pageIndex: Int): String
}
