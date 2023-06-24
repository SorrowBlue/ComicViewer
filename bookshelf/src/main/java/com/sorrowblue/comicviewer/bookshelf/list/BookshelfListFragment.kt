package com.sorrowblue.comicviewer.bookshelf.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoEditResult
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoRemoveResult
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfListFragment : FrameworkFragment() {

    private val viewModel: BookshelfListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Mdc3Theme {
                    BookshelfListScreen(Modifier, findNavController(), viewModel)
                }
            }
        }
    }
}
