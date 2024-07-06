package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

/**
 * フォルダ表示設定
 *
 * @property display ファイル/フォルダの表示方式。
 * @property isEnabledThumbnail サムネイルを表示するか
 * @property columnSize グリッド表示時のカラムサイズ
 * @property sortType ソート順所
 * @property showHiddenFile 隠しファイルを表示するか
 */
@Serializable
data class FolderDisplaySettings(
    val display: Display = Display.Grid,
    val columnSize: ColumnSize = ColumnSize.Medium,
    val sortType: SortType = SortType.NAME(true),
    val showHiddenFile: Boolean = false,
    val isEnabledThumbnail: Boolean = true,
    val fontSize: Int = 16,
) {

    enum class ColumnSize {
        Medium, Large
    }

    enum class Display {
        List,
        Grid,
    }
}
