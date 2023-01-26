package com.sorrowblue.comicviewer.favorite.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.favorite.FavoriteAdapter
import com.sorrowblue.comicviewer.favorite.FavoriteFragmentArgs
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentListBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FavoriteListFragment : FrameworkFragment(R.layout.favorite_fragment_list) {

    private val binding: FavoriteFragmentListBinding by viewBinding()
    private val viewModel: FavoriteListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.setupWithNavController(findNavController())
        val adapter = FavoriteAdapter {
            findNavController().navigate(
                FavoriteListFragmentDirections.actionFavoriteListToFavorite(
                    it.id
                )
            )
        }
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest(adapter::submitData)
        }
        binding.fab.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.favorite_list_fragment) {
                findNavController().navigate(FavoriteListFragmentDirections.actionFavoriteListToFavoriteCreate())
            }
        }
        adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
            .map { it.refresh is LoadState.NotLoading && adapter.itemCount == 0 }
            .onEach { viewModel.isEmptyData.value = it }
            .launchInWithLifecycle()
    }
}

private fun FavoriteListFragmentDirections.Companion.actionFavoriteListToFavorite(favoriteId: FavoriteId) =
    object : NavDirections {
        override val actionId = actionFavoriteListToFavorite().actionId
        override val arguments = FavoriteFragmentArgs(favoriteId.value).toBundle()
    }

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase
) : ViewModel() {

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(10)))
            .cachedIn(viewModelScope)

    val isEmptyData = MutableStateFlow(false)
}
