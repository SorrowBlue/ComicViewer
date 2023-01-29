package com.sorrowblue.comicviewer.favorite.list

import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteItemListBinding
import com.sorrowblue.comicviewer.favorite.extensiton.transitionName
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class FavoriteListAdapter(private val onClick: (Favorite, FragmentNavigator.Extras) -> Unit) :
    PagingDataAdapter<Favorite, FavoriteListAdapter.ViewHolder>(object :
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
        ViewBindingViewHolder<FavoriteItemListBinding>(parent, FavoriteItemListBinding::inflate) {

        fun bind(item: Favorite) {
            binding.favorite = item
            binding.root.setOnClickListener {
                onClick(item, FragmentNavigatorExtras(it to item.transitionName))
            }
        }
    }
}
