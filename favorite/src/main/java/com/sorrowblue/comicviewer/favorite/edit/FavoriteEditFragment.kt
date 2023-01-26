package com.sorrowblue.comicviewer.favorite.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentEditBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FavoriteEditFragment : FrameworkFragment(R.layout.favorite_fragment_edit) {

    private val binding: FavoriteFragmentEditBinding by viewBinding()
    private val viewModel: FavoriteEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.setupWithNavController(findNavController())
        setupAdapter()
        binding.save.setOnClickListener {
            viewModel.save {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupAdapter() {
        val adapter = FavoriteBookAdapter {
            viewModel.remove(it)
        }
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }
}

