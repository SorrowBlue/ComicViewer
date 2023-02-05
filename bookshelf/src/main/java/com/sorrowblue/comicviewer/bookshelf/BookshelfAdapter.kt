package com.sorrowblue.comicviewer.bookshelf

import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.sorrowblue.comicviewer.bookshelf.viewholder.BookshelfViewHolder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings

class BookshelfAdapter(
    display: BookshelfDisplaySettings.Display,
    private val onClick: (File, String, FragmentNavigator.Extras) -> Unit,
    private val onLongClick: (File) -> Unit
) : PagingDataAdapter<File, BookshelfViewHolder<out ViewBinding>>(object :
    DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }
}) {

    var display = display
        set(value) {
            field = value
            refresh()
        }

    override fun getItemViewType(position: Int) = display.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (display) {
            BookshelfDisplaySettings.Display.GRID ->
                BookshelfViewHolder.Grid(parent, onClick, onLongClick)

            BookshelfDisplaySettings.Display.LIST ->
                BookshelfViewHolder.List(parent, onClick, onLongClick)
        }

    override fun onBindViewHolder(holder: BookshelfViewHolder<out ViewBinding>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: BookshelfViewHolder<out ViewBinding>) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}
