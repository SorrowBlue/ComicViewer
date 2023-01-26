package com.sorrowblue.comicviewer.bookshelf

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sorrowblue.comicviewer.bookshelf.viewholder.BookshelfViewHolder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import logcat.logcat

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }
}

class BookshelfAdapter(display: BookshelfDisplaySettings.Display) :
    PagingDataAdapter<File, BookshelfViewHolder<out ViewBinding>>(DIFF_CALLBACK) {
    var display = display
        set(value) {
            field = value
            refresh()
        }

    override fun getItemViewType(position: Int) = display.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (display) {
            BookshelfDisplaySettings.Display.GRID -> BookshelfViewHolder.Grid(parent)
            BookshelfDisplaySettings.Display.LIST -> BookshelfViewHolder.List(parent)
        }

    override fun onBindViewHolder(holder: BookshelfViewHolder<out ViewBinding>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: BookshelfViewHolder<out ViewBinding>) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}

class GridItemOffsetDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    companion object {

        @JvmStatic
        fun create(sp: Float) = GridItemOffsetDecoration(sp.toInt())
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
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
