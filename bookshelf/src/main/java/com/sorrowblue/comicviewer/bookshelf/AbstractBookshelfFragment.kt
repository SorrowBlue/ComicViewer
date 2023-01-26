package com.sorrowblue.comicviewer.bookshelf

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentBinding
import com.sorrowblue.comicviewer.domain.PagingException
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.launchIn
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
abstract class AbstractBookshelfFragment : FrameworkFragment(R.layout.bookshelf_fragment),
    Toolbar.OnMenuItemClickListener {

    protected val binding: BookshelfFragmentBinding by viewBinding()
    protected abstract val viewModel: AbstractBookshelfViewModel
    private val commonViewModel: CommonViewModel by activityViewModels()

    protected abstract val menuResId: Int

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
        setupAdapter()
        if (!viewModel.isInitialize) {
            viewModel.isInitialize = true
            binding.recyclerView.doOnPreDraw {
                viewLifecycleOwner.lifecycleScope.launch {
                    startPostponedEnterTransition()
                }
            }
        }

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

        setupSearchAdapter()
    }

    private fun setupAdapter() {
        val adapter =
            BookshelfAdapter(runBlocking { viewModel.bookshelfDisplaySettingsFlow.first().display })
        binding.recyclerView.adapter = adapter

        viewModel.pagingDataFlow.onEach { adapter.submitDataWithLifecycle(it) }
            .launchIn()

        if (viewModel.isInitialize) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .first { it.refresh is LoadState.NotLoading && adapter.itemCount > 0 }
                    startPostponedEnterTransition()
                }
            }
        }
        binding.recyclerView.addItemDecoration(GridItemOffsetDecoration(4))

        viewModel.bookshelfDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()

        viewModel.spanCountFlow.onEach(binding.recyclerView::setSpanCount)
            .launchInWithLifecycle()

        adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
            .map { it.refresh is LoadState.Loading }
            .onEach { viewModel.isRefreshing.value = it }
            .launchInWithLifecycle()

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                viewModel.isEmptyData.value =
                    it.mediator?.refresh is LoadState.NotLoading && it.mediator?.append?.endOfPaginationReached == true && adapter.itemCount == 0
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.mapNotNull { it.refresh as? LoadState.Error }
                .distinctUntilChanged()
                .collectLatest {
                    if (it.error is PagingException) {
                        Snackbar.make(
                            binding.root,
                            (it.error as PagingException).getMessage(requireContext()),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        if (viewModel.position >= 0) {
            val position = viewModel.position
            viewModel.position = -1
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .first { it.refresh is LoadState.NotLoading && adapter.itemCount > 0 }
                    binding.recyclerView.scrollToPosition(position)
                    commonViewModel.isRestored.emit(true)
                }
            }
        }
    }

    private fun setupSearchAdapter() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN
        ) {
            if (binding.searchView.isShown) {
                binding.searchView.hide()
            }
        }
        binding.searchView.addTransitionListener { _, _, newState ->
            callback.isEnabled =
                newState == SearchView.TransitionState.SHOWING || newState == SearchView.TransitionState.SHOWN
        }
        val adapter = BookshelfAdapter(BookshelfDisplaySettings.Display.LIST)
        binding.recyclerView.setSpanCount(1)
        binding.searchRecyclerView.adapter = adapter
        binding.searchView.editText.doAfterTextChanged { editable ->
            editable?.toString()?.let {
                if (viewModel.query != it) {
                    viewModel.query = it
                    adapter.refresh()
                }
            }
        }

        viewModel.pagingQueryDataFlow.onEach {
            adapter.submitDataWithLifecycle(it)
        }.launchIn()
    }
}

private fun PagingException.getMessage(context: Context): String {
    return when (this) {
        PagingException.NoNetwork -> "ネットワークに接続していません。"
        PagingException.InvalidAuth -> "無効な認証情報"
        PagingException.InvalidServer -> "サーバーが見つかりません"
        PagingException.NotFound -> "ファイル/フォルダが見つかりません。"
    }
}

context(Fragment)
fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.submitDataWithLifecycle(data: PagingData<T>) {
    submitData(viewLifecycleOwner.lifecycle, data)
}
