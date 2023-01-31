package com.sorrowblue.comicviewer.server.management.device

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.server.management.R
import com.sorrowblue.comicviewer.server.management.databinding.ServerManagementFragmentDeviceBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class ServerManagementDeviceFragment :
    FrameworkFragment(R.layout.server_management_fragment_device) {

    private val binding: ServerManagementFragmentDeviceBinding by viewBinding()
    private val viewModel: ServerManagementDeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.transitionName != null) {
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
            enterTransition = null
            returnTransition = null
        } else {
            sharedElementEnterTransition = null
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())
        binding.root.transitionName = viewModel.transitionName
        binding.viewModel = viewModel
        binding.dir.setEndIconOnClickListener {
            openDirectory()
        }
        binding.fab.setOnClickListener {
            viewModel.connect {
                findNavController().popBackStack(R.id.server_management_selection_fragment, true)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isError.collectLatest {
                binding.fab.isEnabled = !it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.message.collectLatest {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.fab)
                        .show()
                }
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            logcat { "data=${it.data?.data}" }
            it.data?.data?.let { uri ->
                viewModel.data.value = uri
            } ?: kotlin.run {
                viewModel.data.value = null
                Snackbar.make(binding.root, "操作がキャンセルされました", Snackbar.LENGTH_SHORT).show()
            }
        }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        activityResultLauncher.launch(intent)
    }
}
