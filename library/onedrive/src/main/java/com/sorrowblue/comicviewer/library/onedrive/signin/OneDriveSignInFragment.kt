package com.sorrowblue.comicviewer.library.onedrive.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.onedrive.R
import com.sorrowblue.comicviewer.library.onedrive.databinding.OnedriveFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class OneDriveSignInFragment : FrameworkFragment(R.layout.onedrive_fragment_signin) {

    private val binding: OnedriveFragmentSigninBinding by viewBinding()
    private val viewModel: OneDriveSignInViewModel by viewModels()

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

        binding.signIn.setOnClickListener {
            viewModel.signIn(requireActivity()) {
                findNavController().navigate(OneDriveSignInFragmentDirections.onedriveActionOneDriveSigninFragmentToOneDriveListFragment())
            }
        }
    }
}