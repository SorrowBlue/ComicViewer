package com.sorrowblue.comicviewer.data.database.entity.collection

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.SearchCondition

internal data class SearchConditionEntity(
    val query: String,
    val range: Range,
    @ColumnInfo("range_parent") val rangeParent: String,
    val period: SearchCondition.Period,
    @ColumnInfo("sort_type") val sortType: SortType,
    @ColumnInfo("sort_type_asc") val sortTypeAsc: Boolean,
    @ColumnInfo("show_hidden") val showHidden: Boolean,
) {
    enum class Range {
        Bookshelf,
        InFolder,
        SubFolder,
    }

    enum class SortType {
        Name, Date, Size
    }

    fun toModel(): SearchCondition {
        return SearchCondition(query)
    }

    companion object {
        fun fromModel(model: SearchCondition): SearchConditionEntity {
            return SearchConditionEntity(
                query = model.query,
                range = when (model.range) {
                    SearchCondition.Range.Bookshelf -> Range.Bookshelf
                    is SearchCondition.Range.InFolder -> Range.InFolder
                    is SearchCondition.Range.SubFolder -> Range.SubFolder
                },
                rangeParent = when (val range = model.range) {
                    SearchCondition.Range.Bookshelf -> ""
                    is SearchCondition.Range.InFolder -> range.parent
                    is SearchCondition.Range.SubFolder -> range.parent
                },
                period = model.period,
                sortType = when (model.sortType) {
                    is com.sorrowblue.comicviewer.domain.model.settings.folder.SortType.Name -> SortType.Name
                    is com.sorrowblue.comicviewer.domain.model.settings.folder.SortType.Date -> SortType.Date
                    is com.sorrowblue.comicviewer.domain.model.settings.folder.SortType.Size -> SortType.Size
                },
                sortTypeAsc = model.sortType.isAsc,
                showHidden = model.showHidden,
            )
        }
    }
}
