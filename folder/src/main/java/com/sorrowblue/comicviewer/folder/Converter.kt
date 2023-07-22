package com.sorrowblue.comicviewer.folder

import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.settings.SortType

object Converter {

    fun searchRangeToButtonId(searchRange: SearchCondition.Range): Int {
        return when (searchRange) {
            SearchCondition.Range.BOOKSHELF -> R.id.folder_search_range_bookshelf
            is SearchCondition.Range.IN_FOLDER -> R.id.folder_search_range_in_folder
            is SearchCondition.Range.FOLDER_BELOW -> R.id.folder_search_range_folder_below
        }
    }

    fun buttonIdToSearchRange(id: List<Int>): SearchCondition.Range {
        return when (id.firstOrNull()) {
            R.id.folder_search_range_in_folder -> SearchCondition.Range.IN_FOLDER("")
            R.id.folder_search_range_folder_below -> SearchCondition.Range.FOLDER_BELOW("")
            else -> SearchCondition.Range.BOOKSHELF
        }
    }

    fun searchPeriodToButtonId(searchPeriod: SearchCondition.Period): Int {
        return when (searchPeriod) {
            SearchCondition.Period.NONE -> R.id.folder_search_period_no_period_specified
            SearchCondition.Period.HOUR_24 -> R.id.folder_search_period_within_24_hours
            SearchCondition.Period.WEEK_1 -> R.id.folder_search_period_within_1_week
            SearchCondition.Period.MONTH_1 -> R.id.folder_search_period_within_1_month
        }
    }

    fun buttonIdToSearchPeriod(id: List<Int>): SearchCondition.Period {
        return when (id.firstOrNull()) {
            R.id.folder_search_period_within_24_hours -> SearchCondition.Period.HOUR_24
            R.id.folder_search_period_within_1_week -> SearchCondition.Period.WEEK_1
            R.id.folder_search_period_within_1_month -> SearchCondition.Period.MONTH_1
            else -> SearchCondition.Period.NONE
        }
    }

    fun searchSortToButtonId(searchSort: SortType): Int {
        return when (searchSort) {
            is SortType.DATE -> R.id.folder_search_sort_date
            is SortType.NAME -> R.id.folder_search_sort_name
            is SortType.SIZE -> R.id.folder_search_sort_size
        }
    }

    fun buttonIdToSearchSort(id: List<Int>, isAsc: Boolean): SortType {
        return when (id.firstOrNull()) {
            R.id.folder_search_sort_date -> SortType.DATE(isAsc)
            R.id.folder_search_sort_size -> SortType.SIZE(isAsc)
            else -> SortType.NAME(isAsc)
        }
    }

    fun searchOrderToButtonId(isAsc: Boolean): Int {
        return if (isAsc) R.id.folder_search_order_asc else R.id.folder_search_order_desc
    }

    fun buttonIdToSearchOrder(id: List<Int>): Boolean {
        return when (id.firstOrNull()) {
            R.id.folder_search_order_desc -> false
            else -> true
        }
    }
}
