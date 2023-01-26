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
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemBinding
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfItemListBinding
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSrcCompat

sealed class BookshelfViewHolder<V : ViewBinding>(
    parent: ViewGroup,
    inflate: (LayoutInflater, ViewGroup, Boolean) -> V,
) : ViewBindingViewHolder<V>(parent, inflate) {

    abstract fun bind(file: File?)
    abstract fun clear()

    protected fun transition(
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
                com.sorrowblue.comicviewer.book.R.id.book_navigation,
                BookFragmentArgs(
                    file.serverId.value,
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

        override fun bind(file: File?) {
            binding.name.text = file?.name.orEmpty()
            val data = file?.let { FileThumbnailRequest(file.serverId to it) }
            when (file) {
                is Bookshelf -> {
                    binding.folderThumbnail.isVisible = true
                    binding.root.transitionName = file.path
                    binding.folderThumbnail.setSrcCompat(data)
                    binding.bookThumbnail.isVisible = false
                    binding.bookThumbnail.transitionName = null
                    binding.bookThumbnail.setSrcCompat(null)
                    binding.progress.isVisible = false
                    binding.progress.max = 0
                    binding.progress.progress = 0
                }

                is Book -> {
                    binding.bookThumbnail.isVisible = true
                    binding.bookThumbnail.transitionName = file.path
                    binding.bookThumbnail.setSrcCompat(data, true)
                    binding.folderThumbnail.isVisible = false
                    binding.root.transitionName = null
                    binding.folderThumbnail.setSrcCompat(null)
                    binding.progress.isVisible = file.totalPageCount > 0 && file.lastPageRead > 0
                    binding.progress.max = file.totalPageCount
                    binding.progress.progress = file.lastPageRead
                }

                null -> {
                    binding.bookThumbnail.isVisible = false
                    binding.bookThumbnail.transitionName = null
                    binding.bookThumbnail.setSrcCompat(null)
                    binding.folderThumbnail.isVisible = false
                    binding.root.transitionName = null
                    binding.folderThumbnail.setSrcCompat(null)
                    binding.progress.isVisible = false
                    binding.name.text = null
                }
            }
            binding.root.setOnLongClickListener {
                when (file) {
                    is File -> {
                        it.findNavController()
                            .navigate(
                                com.sorrowblue.comicviewer.book.R.id.book_info_navigation,
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
                        file, when (file) {
                            is Book -> binding.bookThumbnail
                            is Bookshelf -> binding.root
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
            binding.folder.dispose()
        }

        override fun bind(file: File?) {
            ViewCompat.setTransitionName(binding.root, file?.path)
            binding.name.text = file?.name.orEmpty()
            var memoryCacheKey: String? = null
            when (file) {
                is Book -> {
                    binding.page.isVisible = true
                    binding.page.text = "${file.totalPageCount}p"
                    binding.cover.transitionName = file.path
                    binding.cover.isVisible = true
                    binding.folder.isVisible = false
                    binding.cover.load(FileThumbnailRequest(file.serverId to file)) {
                        listener { _, result ->
                            memoryCacheKey = result.memoryCacheKey?.key
                        }
                    }
                }

                is Bookshelf -> {
                    binding.page.isVisible = false
                    binding.cover.isVisible = false
                    binding.folder.isVisible = true
                    binding.folder.transitionName = file.path
                    binding.folder.load(FileThumbnailRequest(file.serverId to file)) {
                        listener { _, result ->
                            memoryCacheKey = result.memoryCacheKey?.key
                        }
                    }
                }

                null -> {
                    binding.name.text = null
                    binding.folder.isVisible = false
                }
            }
            binding.root.setOnClickListener {
                if (file != null) {
                    transition(
                        file,
                        when (file) {
                            is Book -> binding.cover
                            is Bookshelf -> binding.folder
                        },
                        memoryCacheKey
                    )
                }
            }
        }
    }
}
