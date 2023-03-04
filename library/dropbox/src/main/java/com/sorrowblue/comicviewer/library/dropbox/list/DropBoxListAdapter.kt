package com.sorrowblue.comicviewer.library.dropbox.list

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.library.databinding.LibraryItemListBinding

internal class DropBoxListAdapter(private val download: (Book) -> Unit) :
    PagingDataAdapter<File, DropBoxListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File, newItem: File) =
                oldItem.bookshelfId == newItem.bookshelfId && oldItem.path == newItem.path

            override fun areContentsTheSame(oldItem: File, newItem: File) = oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<LibraryItemListBinding>(
            parent,
            LibraryItemListBinding::inflate
        ) {
        fun bind(file: File) {
            binding.icon.load(file.params["preview_url"])
            binding.name.text = file.name
            binding.size.text = "${file.size} B"
            binding.root.setOnClickListener {
                when (file) {
                    is Book -> download(file)
                    is Folder ->
                        it.findNavController()
                            .navigate(DropBoxListFragmentDirections.actionDropboxListSelf(file.path))
                }
            }
        }
    }
}
