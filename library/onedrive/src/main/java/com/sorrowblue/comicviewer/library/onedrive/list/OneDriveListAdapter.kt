package com.sorrowblue.comicviewer.library.onedrive.list

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.library.databinding.GoogledriveItemListBinding

internal class OneDriveListAdapter(private val download: (Book) -> Unit) :
    PagingDataAdapter<File, OneDriveListAdapter.ViewHolder>(
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
        ViewBindingViewHolder<GoogledriveItemListBinding>(
            parent, GoogledriveItemListBinding::inflate
        ) {
        fun bind(file: File) {
            binding.icon.load(file.params["iconLink"])
            binding.name.text = file.name
            binding.size.text = file.size.toString() + " B"
            binding.root.setOnClickListener {
                when (file) {
                    is Book -> download(file)
                    is Folder ->
                        it.findNavController()
                            .navigate(
                                OneDriveListFragmentDirections.actionOneDriveListSelf(
                                    file.params["driveId"],
                                    file.path
                                )
                            )
                }
            }
        }
    }
}
