package com.sorrowblue.comicviewer.bookshelf.manage.smb

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.InverseMethod
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
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentManageSmbBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BookshelfManageSmbFragment :
    FrameworkFragment(R.layout.bookshelf_fragment_manage_smb) {

    private val binding: BookshelfFragmentManageSmbBinding by viewBinding()
    private val viewModel: BookshelfManageSmbViewModel by viewModels()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isConnecting.collectLatest {
                binding.save.isVisible = !it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isError.collectLatest {
                binding.save.isEnabled = !it
            }
        }
        binding.save.setOnClickListener {
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
                        R.id.bookshelf_manage_list_fragment,
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
                    commonViewModel.snackbarMessage.tryEmit(it)
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

