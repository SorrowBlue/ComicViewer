package com.sorrowblue.comicviewer.server

import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.server.databinding.ServerItemListBinding
import com.sorrowblue.comicviewer.server.info.ServerInfoFragmentArgs

internal class ServerListAdapter : PagingDataAdapter<ServerFolder, ServerListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ServerFolder>() {
        override fun areItemsTheSame(oldItem: ServerFolder, newItem: ServerFolder) =
            oldItem.folder.serverId == newItem.folder.serverId && oldItem.folder.path == newItem.folder.path

        override fun areContentsTheSame(oldItem: ServerFolder, newItem: ServerFolder) =
            oldItem.server == newItem.server
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ServerItemListBinding>(parent, ServerItemListBinding::inflate) {

        fun bind(item: ServerFolder) {
            binding.server = item.server
            binding.preview.load(FileThumbnailRequest(item.server.id to item.folder))
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
                    ServerListFragmentDirections.actionServerListToServerInfoNavigation(item.server)
                )
            }
        }

        private fun ServerListFragmentDirections.Companion.actionServerListToFolder(
            folder: Folder,
            transitionName: String
        ) = object : NavDirections {
            override val actionId = actionServerListToFolder().actionId
            override val arguments = FolderFragmentArgs(
                folder.serverId.value,
                folder.path.encodeBase64(),
                transitionName
            ).toBundle()

        }

        private fun ServerListFragmentDirections.Companion.actionServerListToServerInfoNavigation(
            server: Server
        ) = object : NavDirections {
            override val actionId = actionServerListToServerInfoNavigation().actionId
            override val arguments = ServerInfoFragmentArgs(server.id).toBundle()

        }
    }
}
