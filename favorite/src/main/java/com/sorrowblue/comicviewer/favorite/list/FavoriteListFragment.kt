package com.sorrowblue.comicviewer.favorite.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.bookshelf.submitDataWithLifecycle
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFragmentArgs
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentListBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FavoriteListFragment : FrameworkFragment(R.layout.favorite_fragment_list) {

    private val binding: FavoriteFragmentListBinding by viewBinding()
    private val viewModel: FavoriteListViewModel by viewModels()

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
        binding.toolbar.setupWithNavController(findNavController())
        binding.fab.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.favorite_list_fragment) {
                findNavController().navigate(FavoriteListFragmentDirections.actionFavoriteListToFavoriteCreate())
            }
        }
        setupAdapter()
    }

    private fun setupAdapter() {
        val adapter = FavoriteListAdapter { favorite, extras ->
            findNavController().navigate(
                FavoriteListFragmentDirections.actionFavoriteListToFavorite(favorite),
                extras
            )
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest {
                adapter.submitDataWithLifecycle(it)
            }
        }
        adapter.loadStateFlow.map { it.refresh }.distinctUntilChanged()
            .map { it is LoadState.NotLoading && adapter.itemCount == 0 }
            .onEach { viewModel.isEmptyDataFlow.value = it }
            .launchInWithLifecycle()
    }

    private fun FavoriteListFragmentDirections.Companion.actionFavoriteListToFavorite(favorite: Favorite) =
        object : NavDirections {
            override val actionId = actionFavoriteListToFavorite().actionId
            override val arguments =
                FavoriteFragmentArgs(favorite.id.value).toBundle()
        }
}
