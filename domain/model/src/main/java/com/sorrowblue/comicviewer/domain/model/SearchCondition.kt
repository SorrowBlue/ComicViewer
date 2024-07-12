package com.sorrowblue.comicviewer.domain.model

import android.os.Parcelable
import com.sorrowblue.comicviewer.domain.model.SearchCondition.Period
import com.sorrowblue.comicviewer.domain.model.SearchCondition.Range
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.parcelize.Parcelize

/**
 * 検索条件
 *
 * @property query クエリ
 * @property range 範囲
 * @property period 期間
 * @property sortType ソート
 * @property showHidden 隠しファイルを表示するか
 */
@Parcelize
data class SearchCondition(
    val query: String = SearchConditionDefaults.query,
    val range: Range = SearchConditionDefaults.range,
    val period: Period = SearchConditionDefaults.period,
    val sortType: SortType = SearchConditionDefaults.sortType,
    val showHidden: Boolean = SearchConditionDefaults.showHidden,
) : Parcelable {

    sealed interface Range : Parcelable {
        @Parcelize
        data object Bookshelf : Range

        @Parcelize
        data class InFolder(val parent: String) : Range

        @Parcelize
        data class SubFolder(val parent: String) : Range

        companion object {
            val entries by lazy { listOf(Bookshelf, InFolder(""), SubFolder("")) }
        }
    }

    enum class Period {
        None, Hour24, Week1, Month1
    }
}

object SearchConditionDefaults {
    const val query: String = ""
    val range: Range = Range.Bookshelf
    val period: Period = Period.None
    val sortType: SortType = SortType.Name(true)
    const val showHidden: Boolean = false
}
