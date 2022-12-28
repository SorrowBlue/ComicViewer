package com.sorrowblue.comicviewer.server.management.selection

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.server.management.ServerType
import com.sorrowblue.comicviewer.server.management.databinding.ServerManagementItemSelectionBinding
import com.sorrowblue.comicviewer.server.management.device.ServerManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.server.management.smb.ServerManagementSmbFragmentArgs

internal class ServerManagementSelectionAdapter :
    ListAdapter<ServerType, ServerManagementSelectionAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ServerType>() {
            override fun areItemsTheSame(oldItem: ServerType, newItem: ServerType) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: ServerType, newItem: ServerType) =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<ServerManagementItemSelectionBinding>(
            parent,
            ServerManagementItemSelectionBinding::inflate
        ) {

        fun bind(item: ServerType) {
            binding.serverType = item
            binding.root.setOnClickListener {
                val transitionName = ViewCompat.getTransitionName(it)
                val extras = FragmentNavigatorExtras(it to transitionName.orEmpty())
                when (item) {
                    ServerType.SMB -> it.findNavController().navigate(
                        ServerManagementSelectionFragmentDirections.actionServerManagementSelectionToServerManagementNavigationSmb().actionId,
                        ServerManagementSmbFragmentArgs(transitionName = transitionName).toBundle(),
                        null,
                        extras
                    )
                    ServerType.DEVICE -> it.findNavController().navigate(
                        ServerManagementSelectionFragmentDirections.actionServerManagementSelectionToServerManagementNavigationDevice().actionId,
                        ServerManagementDeviceFragmentArgs(transitionName = transitionName).toBundle(),
                        null,
                        extras
                    )
                }
            }
        }
    }
}
