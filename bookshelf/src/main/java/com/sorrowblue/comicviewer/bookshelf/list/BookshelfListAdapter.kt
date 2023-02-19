package com.sorrowblue.comicviewer.bookshelf.list

import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemListBinding
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

internal class BookshelfListAdapter :
    PagingDataAdapter<BookshelfFolder, BookshelfListAdapter.ViewHolder>(
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
        ViewBindingViewHolder<BookshelfItemListBinding>(parent, BookshelfItemListBinding::inflate) {

        fun bind(item: BookshelfFolder) {
            binding.bookshelf = item.bookshelf
            binding.folder = item.folder
            binding.preview.load(item.folder)
            binding.root.setOnClickListener {
                val transitionName = binding.root.transitionName
                val extras = FragmentNavigatorExtras(it to transitionName)
                it.findNavController().navigate(
                    BookshelfListFragmentDirections.actionBookshelfListToFolder(
                        item.folder,
                        transitionName
                    ),
                    extras
                )
            }
            binding.root.setOnLongClickListener {
                it.findNavController().navigate(
                    BookshelfListFragmentDirections.actionBookshelfListToBookshelfInfo(item.bookshelf.id.value)
                )
                true
            }
        }

        private fun BookshelfListFragmentDirections.Companion.actionBookshelfListToFolder(
            folder: Folder,
            transitionName: String
        ) = object : NavDirections {
            override val actionId = actionBookshelfListToFolder().actionId
            override val arguments = FolderFragmentArgs(
                folder.bookshelfId.value,
                folder.path.encodeBase64(),
                transitionName
            ).toBundle()

        }
    }
}
