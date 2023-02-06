package com.sorrowblue.comicviewer.server

import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.server.databinding.ServerItemListBinding
import com.sorrowblue.comicviewer.server.info.ServerInfoFragmentArgs

internal class ServerListAdapter : PagingDataAdapter<BookshelfFolder, ServerListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<BookshelfFolder>() {
        override fun areItemsTheSame(oldItem: BookshelfFolder, newItem: BookshelfFolder) =
            oldItem.folder.bookshelfId == newItem.folder.bookshelfId && oldItem.folder.path == newItem.folder.path

        override fun areContentsTheSame(oldItem: BookshelfFolder, newItem: BookshelfFolder) =
            oldItem.bookshelf == newItem.bookshelf
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ServerItemListBinding>(parent, ServerItemListBinding::inflate) {

        fun bind(item: BookshelfFolder) {
            binding.server = item.bookshelf
            binding.preview.load(FileThumbnailRequest(item.bookshelf.id to item.folder))
            binding.root.setOnClickListener {
                val transitionName = binding.root.transitionName
                val extras = FragmentNavigatorExtras(it to transitionName)
                it.findNavController().navigate(
                    ServerListFragmentDirections.actionServerListToFolder(
                        item.folder,
                        transitionName
                    ),
                    extras
                )
            }
            binding.menu.setOnClickListener {
                it.findNavController().navigate(
                    ServerListFragmentDirections.actionServerListToServerInfoNavigation(item.bookshelf)
                )
            }
        }

        private fun ServerListFragmentDirections.Companion.actionServerListToFolder(
            folder: Folder,
            transitionName: String
        ) = object : NavDirections {
            override val actionId = actionServerListToFolder().actionId
            override val arguments = FolderFragmentArgs(
                folder.bookshelfId.value,
                folder.path.encodeBase64(),
                transitionName
            ).toBundle()

        }

        private fun ServerListFragmentDirections.Companion.actionServerListToServerInfoNavigation(
            bookshelf: Bookshelf
        ) = object : NavDirections {
            override val actionId = actionServerListToServerInfoNavigation().actionId
            override val arguments = ServerInfoFragmentArgs(bookshelf.id.value).toBundle()

        }
    }
}
