package com.sorrowblue.comicviewer.bookshelf.list

import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs

internal class BookshelfListAdapter :
    PagingDataAdapter<BookshelfFolder, BookshelfListAdapter.ArticleViewHolder>(
        object : DiffUtil.ItemCallback<BookshelfFolder>() {
            override fun areItemsTheSame(oldItem: BookshelfFolder, newItem: BookshelfFolder) =
                oldItem.folder.bookshelfId == newItem.folder.bookshelfId && oldItem.folder.path == newItem.folder.path

            override fun areContentsTheSame(oldItem: BookshelfFolder, newItem: BookshelfFolder) =
                oldItem.bookshelf == newItem.bookshelf
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArticleViewHolder(BookshelfFolderRowComposeView(parent.context))

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let { holder.bindTo(it) }
    }

    class ArticleViewHolder(private val composeView: BookshelfFolderRowComposeView) :
        RecyclerView.ViewHolder(composeView) {
        fun bindTo(bookshelfFolder: BookshelfFolder) {
            composeView.onClick = {
                composeView.findNavController().navigate(
                    BookshelfListFragmentDirections.actionBookshelfListToFolder(bookshelfFolder.folder,)
                )
            }
            composeView.onLongClick = {
                composeView.findNavController().navigate(
                    BookshelfListFragmentDirections.actionBookshelfListToBookshelfInfo(
                        bookshelfFolder.bookshelf.id.value
                    )
                )
            }
            composeView.bookshelfFolder = bookshelfFolder
        }

        private fun BookshelfListFragmentDirections.Companion.actionBookshelfListToFolder(
            folder: Folder,
        ) = object : NavDirections {
            override val actionId = actionBookshelfListToFolder().actionId
            override val arguments = FolderFragmentArgs(
                folder.bookshelfId.value,
                folder.base64Path(),
            ).toBundle()

        }
    }
}
