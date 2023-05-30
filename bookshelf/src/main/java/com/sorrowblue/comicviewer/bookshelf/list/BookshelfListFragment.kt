package com.sorrowblue.comicviewer.bookshelf.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentListBinding
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoEditResult
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoRemoveResult
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResultListener
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class BookshelfListFragment :
    PagingFragment<BookshelfFolder>(R.layout.bookshelf_fragment_list),
    Toolbar.OnMenuItemClickListener {

    private val binding: BookshelfFragmentListBinding by viewBinding()
    override val viewModel: BookshelfListViewModel by viewModels()

    override val adapter get() = BookshelfListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController()
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        binding.empty.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        fab.setOnClickListener {
            findNavController().navigate(BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageList())
        }

        setDialogFragmentResultListener<BookshelfInfoRemoveResult>(
            R.id.bookshelf_list_fragment,
            "remove"
        ) {
            findNavController().navigate(
                BookshelfListFragmentDirections.actionBookshelfListToBookshelfRemoveConfirm(it.bookshelfId.value)
            )
        }

        setDialogFragmentResultListener<BookshelfInfoEditResult>(
            R.id.bookshelf_list_fragment,
            "edit"
        ) {
            when (it.type) {
                "InternalStorage" -> {
                    findNavController().navigate(
                        BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageDevice(
                            it.bookshelfId.value
                        )
                    )
                }

                "SmbServer" -> {
                    findNavController().navigate(
                        BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageSmb(it.bookshelfId.value)
                    )
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem) =
        item.onNavDestinationSelected(findNavController())
}
