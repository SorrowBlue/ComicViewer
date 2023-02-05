package com.sorrowblue.comicviewer.bookshelf

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemOffsetDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    companion object {

        @JvmStatic
        fun create(sp: Float) = GridItemOffsetDecoration(sp.toInt())
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        val totalSpanCount = getTotalSpanCount(parent)
        val spanSize = getItemSpanSize(parent, position)

        outRect.top = if (isInTheFirstRow(position, totalSpanCount)) 0 else spacing
        outRect.left = if (isFirstInRow(position, totalSpanCount, spanSize)) 0 else spacing / 2
        outRect.right = if (isLastInRow(position, totalSpanCount, spanSize)) 0 else spacing / 2
        outRect.bottom = 0
    }

    private fun isInTheFirstRow(position: Int, totalSpanCount: Int): Boolean =
        position < totalSpanCount

    private fun isFirstInRow(position: Int, totalSpanCount: Int, spanSize: Int): Boolean =
        if (totalSpanCount != spanSize) {
            position % totalSpanCount == 0
        } else true

    private fun isLastInRow(position: Int, totalSpanCount: Int, spanSize: Int): Boolean =
        isFirstInRow(position + 1, totalSpanCount, spanSize)

    private fun getTotalSpanCount(parent: RecyclerView): Int =
        (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1

    private fun getItemSpanSize(parent: RecyclerView, position: Int): Int =
        (parent.layoutManager as? GridLayoutManager)?.spanSizeLookup?.getSpanSize(position) ?: 1

}
