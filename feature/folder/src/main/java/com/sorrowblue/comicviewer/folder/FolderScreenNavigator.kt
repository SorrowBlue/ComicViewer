package com.sorrowblue.comicviewer.folder

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File

/**
 * フォルダ画面のナビゲータ
 */
interface FolderScreenNavigator {

    /**
     * ナビゲートを行う
     */
    fun navigateUp()

    /**
     * 検索ボタンが押されたとき
     *
     * @param bookshelfId　本棚ID
     * @param path　検索するパス
     */
    fun onSearchClick(bookshelfId: BookshelfId, path: String)

    /**
     * 設定ボタンが押されたとき
     */
    fun onSettingsClick()

    /**
     * ファイルがクリックされたとき
     *
     * @param file　クリックされたファイル
     */
    fun onFileClick(file: File)

    /**
     * お気に入りボタンが押されたとき
     *
     * @param file　お気に入りにするファイル
     */
    fun onFavoriteClick(file: File)
}
