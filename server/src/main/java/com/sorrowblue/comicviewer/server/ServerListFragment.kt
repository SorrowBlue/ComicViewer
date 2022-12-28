package com.sorrowblue.comicviewer.server

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.google.android.material.transition.MaterialElevationScale
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.server.databinding.ServerFragmentListBinding
import com.sorrowblue.comicviewer.server.management.selection.ServerManagementSelectionFragmentArgs
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class ServerListFragment : FrameworkFragment(R.layout.server_fragment_list),
    Toolbar.OnMenuItemClickListener {

    private val binding: ServerFragmentListBinding by viewBinding()
    private val viewModel: ServerListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }

        binding.recyclerView.applyInsetter {
            type(
                navigationBars = true,
                captionBar = true,
                displayCutout = true
            ) { padding(left = true, right = true, bottom = true) }
        }

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener(this)

        binding.fab.setOnClickListener {
            findNavController().navigate(
                ServerListFragmentDirections.actionServerListToServerManagementNavigationSelection().actionId,
                ServerManagementSelectionFragmentArgs(it.transitionName).toBundle(),
                null,
                FragmentNavigatorExtras(it to it.transitionName)
            )
        }

        setupAdapter()
    }

    override fun onMenuItemClick(item: MenuItem) =
        item.onNavDestinationSelected(findNavController())

    private fun setupAdapter() {
        val adapter = ServerListAdapter()
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }.map { adapter.itemCount == 0 }
                .collectLatest { viewModel.isEmptyData.value = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest(adapter::submitData)
        }
    }
}
