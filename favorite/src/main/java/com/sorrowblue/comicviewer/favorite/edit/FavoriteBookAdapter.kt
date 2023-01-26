package com.sorrowblue.comicviewer.favorite.edit

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.dispose
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteItemListBinding
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class FavoriteBookAdapter(private val onClickRemove: (File) -> Unit) :
    PagingDataAdapter<File, FavoriteBookAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: File, newItem: File) =
            oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<FavoriteItemListBinding>(parent, FavoriteItemListBinding::inflate) {

        fun bind(file: File?) {
            ViewCompat.setTransitionName(binding.root, file?.path)
            binding.name.text = file?.name.orEmpty()
            binding.path.text = file?.parent.orEmpty()
            binding.remove.setOnClickListener {
                onClickRemove(file!!)
            }
            when (file) {
                is Book -> {
                    binding.thumbnail.load(FileThumbnailRequest(file.serverId to file)) {
                    }
                }
                is Bookshelf -> {
                }
                null -> {
                }
            }
        }

        fun clear() {
            binding.thumbnail.dispose()
        }
    }
}
