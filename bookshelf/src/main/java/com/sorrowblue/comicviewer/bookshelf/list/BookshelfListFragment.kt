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
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
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
