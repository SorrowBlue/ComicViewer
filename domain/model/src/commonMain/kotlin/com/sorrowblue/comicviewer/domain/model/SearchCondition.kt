package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.serialization.Serializable

/**
 * 検索条件
 *
 * @property query クエリ
 * @property range 範囲
 * @property period 期間
 * @property sortType ソート
 * @property showHidden 隠しファイルを表示するか
 */
@Serializable
data class SearchCondition(
    val query: String = "",
    val range: Range = Range.Bookshelf,
    val period: Period = Period.None,
    val sortType: SortType = SortType.Name(true),
    val showHidden: Boolean = false,
) {

    sealed interface Range {
        data object Bookshelf : Range

        data class InFolder(val parent: String) : Range

        data class SubFolder(val parent: String) : Range

        companion object {
            val entries by lazy { listOf(Bookshelf, InFolder(""), SubFolder("")) }
        }
    }

    enum class Period {
        None, Hour24, Week1, Month1
    }
}
