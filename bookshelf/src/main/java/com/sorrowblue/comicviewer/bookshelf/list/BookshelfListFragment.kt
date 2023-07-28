package com.sorrowblue.comicviewer.bookshelf.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfListFragment : FrameworkFragment() {

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
                AppMaterialTheme {
                    BookshelfListScreen(findNavController() as NavHostController)
                }
            }
        }
    }
}
