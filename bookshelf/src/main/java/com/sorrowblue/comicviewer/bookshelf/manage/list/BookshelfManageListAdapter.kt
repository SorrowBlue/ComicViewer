package com.sorrowblue.comicviewer.bookshelf.manage.list

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemManageListBinding
import com.sorrowblue.comicviewer.bookshelf.manage.BookshelfSource
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class BookshelfManageListAdapter :
    ListAdapter<BookshelfSource, BookshelfManageListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<BookshelfSource>() {
            override fun areItemsTheSame(oldItem: BookshelfSource, newItem: BookshelfSource) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: BookshelfSource, newItem: BookshelfSource) =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<BookshelfItemManageListBinding>(
            parent,
            BookshelfItemManageListBinding::inflate
        ) {

        fun bind(item: BookshelfSource) {
            binding.source = item
            binding.root.setOnClickListener {
                val transitionName = ViewCompat.getTransitionName(it)
                val extras = FragmentNavigatorExtras(it to transitionName.orEmpty())
                when (item) {
                    BookshelfSource.DEVICE -> it.findNavController().navigate(
                        BookshelfManageListFragmentDirections.actionBookshelfManageListToBookshelfManageDevice(
                            transitionName = transitionName
                        ),
                        extras
                    )

                    BookshelfSource.SMB -> it.findNavController().navigate(
                        BookshelfManageListFragmentDirections.actionBookshelfManageListToBookshelfManageSmb(
                            transitionName = transitionName
                        ),
                        extras
                    )
                }
            }
        }
    }
}
