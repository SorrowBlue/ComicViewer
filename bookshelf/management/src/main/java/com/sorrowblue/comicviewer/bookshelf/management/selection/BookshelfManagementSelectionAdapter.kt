package com.sorrowblue.comicviewer.bookshelf.management.selection

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sorrowblue.comicviewer.bookshelf.management.ServerType
import com.sorrowblue.comicviewer.bookshelf.management.databinding.BookshelfManagementItemSelectionBinding
import com.sorrowblue.comicviewer.bookshelf.management.device.BookshelfManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.bookshelf.management.smb.BookshelfManagementSmbFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class BookshelfManagementSelectionAdapter :
    ListAdapter<ServerType, BookshelfManagementSelectionAdapter.ViewHolder>(
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
        ViewBindingViewHolder<BookshelfManagementItemSelectionBinding>(
            parent,
            BookshelfManagementItemSelectionBinding::inflate
        ) {

        fun bind(item: ServerType) {
            binding.serverType = item
            binding.root.setOnClickListener {
                val transitionName = ViewCompat.getTransitionName(it)
                val extras = FragmentNavigatorExtras(it to transitionName.orEmpty())
                when (item) {
                    ServerType.SMB -> it.findNavController().navigate(
                        BookshelfManagementSelectionFragmentDirections.actionBookshelfManagementSelectionToBookshelfManagementSmb().actionId,
                        BookshelfManagementSmbFragmentArgs(transitionName = transitionName).toBundle(),
                        null,
                        extras
                    )

                    ServerType.DEVICE -> it.findNavController().navigate(
                        BookshelfManagementSelectionFragmentDirections.actionBookshelfManagementSelectionToBookshelfManagementDevice().actionId,
                        BookshelfManagementDeviceFragmentArgs(transitionName = transitionName).toBundle(),
                        null,
                        extras
                    )
                }
            }
        }
    }
}
