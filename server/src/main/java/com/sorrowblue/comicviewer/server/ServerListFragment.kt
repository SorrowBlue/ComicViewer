package com.sorrowblue.comicviewer.server

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.server.databinding.ServerFragmentListBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class ServerListFragment : PagingFragment<ServerFolder>(R.layout.server_fragment_list),
    Toolbar.OnMenuItemClickListener {

    private val binding: ServerFragmentListBinding by viewBinding()
    override val viewModel: ServerListViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override val recyclerView get() = binding.recyclerView
    override val adapter get() = ServerListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonViewModel.isVisibleFab.value = true
        commonViewModel.isVisibleBottomNavigation.value = true

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener(this)
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

        fab.setOnClickListener {
            navigate(ServerListFragmentDirections.actionServerListToServerManagementNavigationSelection())
        }
    }

    override fun onMenuItemClick(item: MenuItem) =
        item.onNavDestinationSelected(findNavController())
}
