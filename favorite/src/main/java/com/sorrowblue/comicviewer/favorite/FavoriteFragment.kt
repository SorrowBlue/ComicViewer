package com.sorrowblue.comicviewer.favorite

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.sorrowblue.comicviewer.bookshelf.BookshelfAdapter
import com.sorrowblue.comicviewer.bookshelf.GridItemOffsetDecoration
import com.sorrowblue.comicviewer.bookshelf.submitDataWithLifecycle
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class FavoriteFragment : FrameworkFragment(R.layout.favorite_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: FavoriteFragmentBinding by viewBinding()
    private val viewModel: FavoriteViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.transitionName != null) {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
                scrimColor = MaterialColors.getColor(
                    requireContext(),
                    android.R.attr.colorBackground,
                    Color.TRANSPARENT
                )
                setPathMotion(MaterialArcMotion())
            }
        }
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
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

        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener(this)

        setupRecyclerView()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_menu_edit -> {
                navigate(FavoriteFragmentDirections.actionFavoriteToFavoriteEdit(viewModel.favoriteId.value))
                true
            }

            R.id.favorite_menu_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("このお気に入りリストを削除しますか？")
                    .setPositiveButton("削除") { _, _ ->
                        viewModel.delete {
                            commonViewModel.snackbarMessage.tryEmit(viewModel.titleFlow.value + "を削除しました。")
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }

            else -> item.onNavDestinationSelected(findNavController())
        }
    }

    private fun setupRecyclerView() {
        val adapter =
            BookshelfAdapter(runBlocking { viewModel.bookshelfDisplaySettingsFlow.first().display })
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest {
                adapter.submitDataWithLifecycle(it)
            }
        }
        if (viewModel.isInitialize) {
            viewModel.isInitialize = true
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .first { it.refresh is LoadState.NotLoading }
                    startPostponedEnterTransition()
                }
            }
        }
        binding.recyclerView.addItemDecoration(GridItemOffsetDecoration(4))

        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
        viewModel.bookshelfDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()

        viewModel.spanCountFlow.onEach(binding.recyclerView::setSpanCount).launchInWithLifecycle()

        adapter.loadStateFlow.map { it.refresh }.distinctUntilChanged()
            .onEach { viewModel.isRefreshingFlow.value = it is LoadState.Loading }
            .launchInWithLifecycle()

        adapter.loadStateFlow.map { it.refresh }.distinctUntilChanged()
            .map { it is LoadState.NotLoading && adapter.itemCount == 0 }
            .onEach { viewModel.isEmptyDataFlow.value = it }
            .launchInWithLifecycle()
    }
}
