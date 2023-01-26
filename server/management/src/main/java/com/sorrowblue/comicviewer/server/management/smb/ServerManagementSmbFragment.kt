package com.sorrowblue.comicviewer.server.management.smb

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.InverseMethod
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
import com.sorrowblue.comicviewer.framework.resource.FrameworkDrawable
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.server.management.R
import com.sorrowblue.comicviewer.server.management.databinding.ServerManagementFragmentSmbBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class ServerManagementSmbFragment :
    FrameworkFragment(R.layout.server_management_fragment_smb) {

    private val binding: ServerManagementFragmentSmbBinding by viewBinding()
    private val viewModel: ServerManagementSmbViewModel by viewModels()

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isConnecting.collectLatest {
                binding.fab.isVisible = !it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isError.collectLatest {
                binding.fab.isEnabled = !it
            }
        }
        binding.fab.setImageResource(FrameworkDrawable.ic_twotone_save_24)
        binding.fab.setOnClickListener {
            binding.host.editText?.setText(viewModel.hostFlow.value)
            if (viewModel.isGuestFlow.value) {
                binding.host.editText?.setText(viewModel.hostFlow.value)
            } else {
                binding.host.editText?.setText(viewModel.hostFlow.value)
                binding.username.editText?.setText(viewModel.usernameFlow.value)
                binding.password.editText?.setText(viewModel.passwordFlow.value)
            }
            WindowInsetsControllerCompat(requireActivity().window, binding.root)
                .hide(WindowInsetsCompat.Type.ime())
            viewModel.connect {
                if (it) {
                    // 初期登録
                    findNavController().popBackStack(
                        R.id.server_management_selection_fragment,
                        true
                    )
                } else {
                    // 編集
                    findNavController().popBackStack()
                }
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
}


internal object AuthConverter {

    @JvmStatic
    @InverseMethod("buttonIdToBoolean")
    fun booleanToButtonId(value: Boolean): Int {
        return if (value) R.id.guest else R.id.username_password
    }

    @JvmStatic
    fun buttonIdToBoolean(value: Int): Boolean {
        return value == R.id.guest
    }
}


internal object PortConverter {

    @JvmStatic
    @InverseMethod("portToString")
    fun stringToPort(value: String?) = value?.toIntOrNull()

    @JvmStatic
    fun portToString(value: Int?) = value?.toString()
}

