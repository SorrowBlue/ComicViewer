package com.sorrowblue.comicviewer.bookshelf.management.device

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.bookshelf.management.R
import com.sorrowblue.comicviewer.bookshelf.management.databinding.BookshelfManagementFragmentDeviceBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class BookshelfManagementDeviceFragment :
    FrameworkFragment(R.layout.bookshelf_management_fragment_device) {

    private val binding: BookshelfManagementFragmentDeviceBinding by viewBinding()
    private val viewModel: BookshelfManagementDeviceViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

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
        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.nestedScroll.applyInsetter {
            type(systemBars = true, displayCutout = true, ime = true) {
                padding(horizontal = true, bottom = true)
            }
        }
        binding.root.transitionName = viewModel.transitionName
        binding.viewModel = viewModel
        binding.dir.setEndIconOnClickListener {
            openDirectory()
        }
        fab.setOnClickListener {
            viewModel.connect {
                findNavController().popBackStack(R.id.bookshelf_management_selection_fragment, true)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isError.collectLatest {
                fab.isEnabled = !it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.message.collectLatest {
                    commonViewModel.snackbarMessage.tryEmit("操作がキャンセルされました")
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
                commonViewModel.snackbarMessage.tryEmit("操作がキャンセルされました")
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
