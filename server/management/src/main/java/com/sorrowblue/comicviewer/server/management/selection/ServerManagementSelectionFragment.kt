package com.sorrowblue.comicviewer.server.management.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.server.management.R
import com.sorrowblue.comicviewer.server.management.ServerType
import com.sorrowblue.comicviewer.server.management.databinding.ServerManagementFragmentSelectionBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class ServerManagementSelectionFragment :
    FrameworkFragment(R.layout.server_management_fragment_selection) {

    private val binding: ServerManagementFragmentSelectionBinding by viewBinding()
    private val viewModel: ServerManagementSelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
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

        val adapter = ServerManagementSelectionAdapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(ServerType.values().asList())
    }
}
