package com.sorrowblue.comicviewer.bookshelf.list

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentListBinding
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoEditResult
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoRemoveResult
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResultListener
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@OptIn(ExperimentalFoundationApi::class)
@AndroidEntryPoint
internal class BookshelfListFragment : FrameworkFragment(R.layout.bookshelf_fragment_list),
    Toolbar.OnMenuItemClickListener {

    private val binding: BookshelfFragmentListBinding by viewBinding()
    private val viewModel: BookshelfListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    fun <T : Any> LazyGridScope.items(
        items: LazyPagingItems<T>,
        key: ((item: T) -> Any)? = null,
        itemContent: @Composable LazyGridItemScope.(item: T?) -> Unit
    ) {
        items(
            count = items.itemCount,
            key = if (key == null) null else { index ->
                val item = items.peek(index)
                if (item == null) {
                    PagingPlaceholderKey(index)
                } else {
                    key(item)
                }
            }
        ) { index ->
            itemContent(items[index])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.composeView.setContent {
            val articles = viewModel.pagingDataFlow.collectAsLazyPagingItems()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(items = articles) {
                    if (it != null) {
                        BookshelfFolderRow(
                            bookshelfFolder = it, modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onLongClick = {
                                        findNavController().navigate(
                                            BookshelfListFragmentDirections.actionBookshelfListToBookshelfInfo(
                                                it.bookshelf.id.value
                                            )
                                        )
                                    },
                                    onClick = {
                                        findNavController().navigate(
                                            BookshelfListFragmentDirections.actionBookshelfListToFolder(
                                                it.folder
                                            )
                                        )
                                    }
                                )
                        )
                    }
                }
            }
        }

        binding.toolbar.setupWithNavController()
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
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

private data class PagingPlaceholderKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
            object : Parcelable.Creator<PagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
            }
    }
}
