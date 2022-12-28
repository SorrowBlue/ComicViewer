package com.sorrowblue.comicviewer.bookshelf

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentBinding
import com.sorrowblue.comicviewer.bookshelf.searchable.IBookshelfViewModel
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal abstract class AbstractBookshelfFragment : FrameworkFragment(R.layout.bookshelf_fragment),
    Toolbar.OnMenuItemClickListener {

    protected val binding: BookshelfFragmentBinding by viewBinding()
    protected abstract val viewModel: IBookshelfViewModel

    protected abstract val menuResId: Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }

        (binding.recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        binding.recyclerView.applyInsetter {
            type(
                navigationBars = true,
                captionBar = true,
                displayCutout = true
            ) { padding(left = true, right = true, bottom = true) }
        }
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.inflateMenu(menuResId)
        binding.toolbar.setOnLongClickListener {
            findNavController().popBackStack(R.id.bookshelf_navigation, true)
            true
        }
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.spanCount.stateIn(viewLifecycleOwner.lifecycleScope).collectLatest {
                binding.recyclerView.setSpanCount(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.serverFlow.filterNotNull().collectLatest {
                setupAdapter(it)
            }
        }
    }

    private suspend fun setupAdapter(server: Server) {

        val adapter = BookshelfAdapter(server, viewModel.settings2.display)
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settings.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                binding.recyclerView.setSpanCount(it.spanCount)
                adapter.display = it.display
                adapter.refresh()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .map { it.refresh is LoadState.Loading }.collectLatest {
                    viewModel.isRefreshing.value = it
                }
        }
        if (viewModel.position > 0) {
            val position = viewModel.position
            viewModel.position = 0
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading && adapter.itemCount > 0 }
                    .collectLatest {
                        binding.recyclerView.scrollToPosition(position)
                        return@collectLatest
                    }
            }
        }
    }
}
