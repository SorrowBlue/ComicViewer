package com.sorrowblue.comicviewer.server

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.bookshelf.BookshelfFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.FileThumbnailRequest
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.server.databinding.ServerItemListBinding
import com.sorrowblue.comicviewer.server.info.ServerInfoFragmentArgs

internal class ServerListAdapter : PagingDataAdapter<ServerBookshelf, ServerListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ServerBookshelf>() {
        override fun areItemsTheSame(oldItem: ServerBookshelf, newItem: ServerBookshelf) =
            oldItem.bookshelf.serverId == newItem.bookshelf.serverId && oldItem.bookshelf.path == newItem.bookshelf.path

        override fun areContentsTheSame(oldItem: ServerBookshelf, newItem: ServerBookshelf) =
            oldItem.server == newItem.server
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ServerItemListBinding>(parent, ServerItemListBinding::inflate) {

        fun bind(item: ServerBookshelf) {
            binding.server = item.server
            binding.preview.load(FileThumbnailRequest(item.server to item.bookshelf))
            binding.root.setOnClickListener {
                val transitionName = binding.root.transitionName
                val extras = FragmentNavigatorExtras(it to transitionName)
                it.findNavController().navigate(
                    ServerListFragmentDirections.actionServerListToBookshelfNavigation().actionId,
                    BookshelfFragmentArgs(
                        item.bookshelf.serverId.value,
                        item.bookshelf.base64Path(),
                        transitionName
                    ).toBundle(),
                    null,
                    extras
                )
            }
            binding.menu.setOnClickListener {
                it.findNavController().navigate(
                    ServerListFragmentDirections.actionServerListToServerInfoNavigation().actionId,
                    ServerInfoFragmentArgs(item.server.id).toBundle()
                )
            }
        }
    }
}
