package com.sorrowblue.comicviewer.favorite.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.bookshelf.submitDataWithLifecycle
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentEditBinding
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
internal class FavoriteEditFragment : FrameworkFragment(R.layout.favorite_fragment_edit) {

    private val binding: FavoriteFragmentEditBinding by viewBinding()
    private val viewModel: FavoriteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.setupWithNavController(findNavController())
        binding.save.setOnClickListener {
            viewModel.save {
                findNavController().popBackStack()
            }
        }
        setupAdapter()
    }

    private fun setupAdapter() {
        val adapter = FavoriteEditAdapter(viewModel::removeFile)
        binding.recyclerView.adapter = adapter
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
}

