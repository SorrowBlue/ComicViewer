package com.sorrowblue.comicviewer.bookshelf

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.sorrowblue.comicviewer.bookshelf.viewholder.BookshelfViewHolder
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }
}

internal class BookshelfAdapter(private val server: Server, var display: BookshelfDisplaySettings.Display) :
    PagingDataAdapter<File, BookshelfViewHolder<out ViewBinding>>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int) = display.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (display) {
            BookshelfDisplaySettings.Display.GRID -> BookshelfViewHolder.Grid(parent)
            BookshelfDisplaySettings.Display.LIST -> BookshelfViewHolder.List(parent)
        }

    override fun onBindViewHolder(holder: BookshelfViewHolder<out ViewBinding>, position: Int) {
        holder.bind(server, getItem(position))
    }

    override fun onViewRecycled(holder: BookshelfViewHolder<out ViewBinding>) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}
