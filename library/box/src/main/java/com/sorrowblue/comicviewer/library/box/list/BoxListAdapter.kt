package com.sorrowblue.comicviewer.library.box.list

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.library.box.databinding.BoxItemListBinding
import logcat.logcat

internal class BoxListAdapter(private val download: (Book) -> Unit) :
    PagingDataAdapter<File, BoxListAdapter.ViewHolder>(
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
        ViewBindingViewHolder<BoxItemListBinding>(parent, BoxItemListBinding::inflate) {
        fun bind(file: File) {
            logcat { file.params["thumbnail"].orEmpty() }
            binding.previewUrl.load(file.params["thumbnail"]) {
                addHeader("Authorization", "Bearer ${file.params["access_token"]}")
            }
            binding.name.text = file.name
            binding.size.text = "${file.size} B"
            binding.root.setOnClickListener {
                when (file) {
                    is Book -> download(file)
                    is Folder ->
                        it.findNavController()
                            .navigate(BoxListFragmentDirections.actionBoxListSelf(file.path))
                }
            }
        }
    }
}
