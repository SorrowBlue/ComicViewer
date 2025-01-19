package com.sorrowblue.comicviewer.folder

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.folder.destinations.SortTypeDialogDestination

/** フォルダ画面のナビゲータ */
interface FolderScreenNavigator {

    val navigator: DestinationsNavigator

    /** ナビゲートを行う */
    fun navigateUp()

    /**
     * 検索ボタンが押されたとき
     *
     * @param bookshelfId　本棚ID
     * @param path　検索するパス
     */
    fun onSearchClick(bookshelfId: BookshelfId, path: String)

    /** 設定ボタンが押されたとき */
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
     * @param bookshelfId 本棚ID
     * @param path お気に入りにするパス
     */
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)

    fun onSortClick(sortType: SortType) {
        navigator.navigate(SortTypeDialogDestination(sortType))
    }

    fun onRestoreComplete() {
    }
}
