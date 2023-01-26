package com.sorrowblue.comicviewer.server.management.selection

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
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
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            containerColor = MaterialColors.getColor(
                requireContext(),
                android.R.attr.colorBackground,
                containerColor
            )
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
        }
        exitTransition = Hold()
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

        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
        binding.recyclerView.applyInsetter {
            type(
                navigationBars = true,
                captionBar = true,
                displayCutout = true
            ) { padding(left = true, right = true, bottom = true) }
        }
        binding.toolbar.setupWithNavController(findNavController())

        val adapter = ServerManagementSelectionAdapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(ServerType.values().asList())
    }
}
