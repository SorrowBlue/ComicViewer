package com.sorrowblue.comicviewer.favorite

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteItemMainBinding
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class FavoriteAdapter(private val onClick: (Favorite) -> Unit) :
    PagingDataAdapter<Favorite, FavoriteAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite) = oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<FavoriteItemMainBinding>(parent, FavoriteItemMainBinding::inflate) {

        fun bind(item: Favorite) {
            binding.favorite = item
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
