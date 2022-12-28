package com.sorrowblue.comicviewer.bookshelf.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewbinding.ViewBinding
import coil.dispose
import coil.load
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.book.info.BookInfoDialogArgs
import com.sorrowblue.comicviewer.bookshelf.BookshelfFragmentArgs
import com.sorrowblue.comicviewer.bookshelf.BookshelfFragmentDirections
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemBinding
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemListBinding
import com.sorrowblue.comicviewer.domain.entity.Book
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.FileThumbnailRequest
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSrcCompat
import logcat.logcat

internal sealed class BookshelfViewHolder<V : ViewBinding>(
    parent: ViewGroup,
    inflate: (LayoutInflater, ViewGroup, Boolean) -> V,
) : ViewBindingViewHolder<V>(parent, inflate) {

    abstract fun bind(server: Server, file: File?)
    abstract fun clear()

    protected fun transition(
        server: Server,
        file: File,
        sharedElement: View,
        memoryCacheKey: String?
    ) {
        val navController = sharedElement.findNavController()
        val transitionName = sharedElement.transitionName
        val extra = FragmentNavigatorExtras(sharedElement to transitionName)
        when (file) {
            is Bookshelf -> {
                navController.navigate(
                    R.id.bookshelf_fragment,
                    BookshelfFragmentArgs(
                        file.serverId.value,
                        file.base64Path(),
                        transitionName
                    ).toBundle(),
                    null,
                    extra
                )
            }
            is Book -> navController.navigate(
                BookshelfFragmentDirections.actionBookshelfToBook().actionId,
                BookFragmentArgs(
                    server.id.value,
                    file.path,
                    transitionName,
                    bindingAdapterPosition,
                    memoryCacheKey
                ).toBundle(),
                null,
                extra
            )
        }
    }


    internal class Grid(parent: ViewGroup) :
        BookshelfViewHolder<BookshelfItemBinding>(parent, BookshelfItemBinding::inflate) {

        override fun clear() {
            binding.bookThumbnail.dispose()
            binding.folderThumbnail.dispose()
        }

        override fun bind(server: Server, file: File?) {
            binding.name.text = file?.name.orEmpty()
            val data = file?.let { FileThumbnailRequest(server to it) }
            when (file) {
                is Bookshelf -> {
                    binding.folderThumbnail.transitionName = file.path
                    binding.bookThumbnail.isVisible = false
                    binding.folderThumbnail.isVisible = true
                    binding.folderThumbnail.load(data)
                    binding.progress.isVisible = false
                    binding.progress.max = 0
                    binding.progress.progress = 0
                }
                is Book -> {
                    binding.bookThumbnail.transitionName = file.path
                    binding.bookThumbnail.isVisible = true
                    binding.folderThumbnail.isVisible = false
                    binding.bookThumbnail.setSrcCompat(data, true)
                    binding.progress.isVisible = file.totalPageCount > 0 && file.lastPageRead > 0
                    binding.progress.max = file.totalPageCount
                    binding.progress.progress = file.lastPageRead
                }
                null -> {
                    binding.bookThumbnail.transitionName = null
                    binding.bookThumbnail.isVisible = false
                    binding.folderThumbnail.isVisible = false
                    binding.name.text = null
                }
            }
            binding.root.setOnLongClickListener {
                when (file) {
                    is File -> {
                        it.findNavController()
                            .navigate(
                                R.id.action_global_book_info_navigation,
                                BookInfoDialogArgs(
                                    file.serverId.value,
                                    file.base64Path()
                                ).toBundle()
                            )
                        true
                    }
                    null -> false
                }
            }
            binding.root.setOnClickListener {
                if (file != null) {
                    transition(
                        server, file, when (file) {
                            is Book -> binding.bookThumbnail
                            is Bookshelf -> binding.folderThumbnail
                        }, data?.hashCode().toString()
                    )
                }
            }
        }
    }

    internal class List(parent: ViewGroup) :
        BookshelfViewHolder<BookshelfItemListBinding>(parent, BookshelfItemListBinding::inflate) {

        override fun clear() {
            binding.cover.dispose()
        }

        override fun bind(server: Server, file: File?) {
            ViewCompat.setTransitionName(binding.root, file?.path)
            binding.name.text = file?.name.orEmpty()
            var memoryCacheKey: String? = null
            when (file) {
                is Book -> {
                    binding.cover.load(FileThumbnailRequest(server to file)) {
                        listener { _, result ->
                            memoryCacheKey = result.memoryCacheKey?.key
                        }
                    }
                    binding.folder.isVisible = false
                    binding.cover.isVisible = true
                }
                is Bookshelf -> {
                    binding.cover.isVisible = false
                    binding.folder.isVisible = true
                }
                null -> {
                    binding.name.text = null
                    binding.folder.isVisible = false
                }
            }
            binding.root.setOnClickListener {
                if (file != null) {
                    transition(
                        server, file, when (file) {
                            is Book -> binding.cover
                            is Bookshelf -> binding.folder
                        }, memoryCacheKey
                    )
                }
            }
        }
    }
}
