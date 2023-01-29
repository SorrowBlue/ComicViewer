package com.sorrowblue.comicviewer.favorite.edit

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.dispose
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteItemEditBinding
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class FavoriteEditAdapter(private val onClickRemove: (File) -> Unit) :
    PagingDataAdapter<File, FavoriteEditAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: File, newItem: File) =
            oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<FavoriteItemEditBinding>(parent, FavoriteItemEditBinding::inflate) {

        fun bind(file: File) {
            binding.file = file
            binding.remove.setOnClickListener {
                onClickRemove(file)
            }
        }

        fun clear() {
            binding.thumbnail.dispose()
        }
    }
}
