package com.sorrowblue.comicviewer.bookshelf.management.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.bookshelf.management.R
import com.sorrowblue.comicviewer.bookshelf.management.ServerType
import com.sorrowblue.comicviewer.bookshelf.management.databinding.BookshelfManagementFragmentSelectionBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class BookshelfManagementSelectionFragment :
    FrameworkFragment(R.layout.bookshelf_management_fragment_selection) {

    private val binding: BookshelfManagementFragmentSelectionBinding by viewBinding()
    private val viewModel: BookshelfManagementSelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController()
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

        val adapter = BookshelfManagementSelectionAdapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(ServerType.values().asList())
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    }
}
