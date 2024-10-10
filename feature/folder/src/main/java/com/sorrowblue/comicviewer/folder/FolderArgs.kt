package com.sorrowblue.comicviewer.folder

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * フォルダ画面の引数
 *
 * @param bookshelfId 本棚ID
 * @param path フォルダのパス
 * @param restorePath 復元するパス (nullの場合は復元しない)
 */
data class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String?,
)
