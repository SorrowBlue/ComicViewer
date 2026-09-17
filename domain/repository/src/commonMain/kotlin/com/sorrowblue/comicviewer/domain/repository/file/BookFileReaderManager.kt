/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book

/**
 * 閲覧中の本に対応する [BookFileReader] のキャッシュおよびライフサイクルを管理するマネージャーポート。
 */
interface BookFileReaderManager {

    /**
     * 指定された本のリーダーを取得する。すでに同じ本が開かれている場合はキャッシュされたリーダーを返す。
     *
     * @param bookshelf 本棚情報
     * @param book 対象の本
     * @return [BookFileReader]
     */
    suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader

    /**
     * 開いている本のリソースを解放する。
     *
     * @param book 閉じる対象の本
     */
    suspend fun close(book: Book)
}
