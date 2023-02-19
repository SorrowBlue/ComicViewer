package com.sorrowblue.comicviewer.library.dropbox.signin

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dropbox.core.android.Auth
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.dropbox.R
import com.sorrowblue.comicviewer.library.dropbox.databinding.DropboxFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class DropBoxSignInFragment : FrameworkFragment(R.layout.dropbox_fragment_signin) {

    private val binding: DropboxFragmentSigninBinding by viewBinding()
    private val viewModel: DropBoxSignInViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.signIn.setOnClickListener {
            Auth.startOAuth2Authentication(requireContext(), "uolcvekf83nd74j")
        }
        binding.progress.isVisible = false
        binding.signIn.isVisible = true
    }

    override fun onResume() {
        super.onResume()
        Auth.getDbxCredential()?.let {
            logcat { "dropbox 認証した" }
            binding.progress.isVisible = true
            binding.signIn.isVisible = false
            viewLifecycleOwner.lifecycleScope.launch {
                logcat { "dropbox 認証情報保存" }
                viewModel.storeCredential(it)
                val account = viewModel.currentAccount().first()
                if (account != null) {
                    logcat { "dropbox アカウント取得" }
                    commonViewModel.snackbarMessage.emit("認証に成功しました。")
                    findNavController().navigate(DropBoxSignInFragmentDirections.actionDropboxSignintoDropboxList())
                } else {
                    logcat { "dropbox アカウント取得失敗" }
                    commonViewModel.snackbarMessage.emit("認証に失敗しました。")
                    binding.progress.isVisible = false
                    binding.signIn.isVisible = true
                }
            }
        } ?: kotlin.run {
            logcat { "dropbox 認証してない" }
            binding.progress.isVisible = false
            binding.signIn.isVisible = true
        }
    }
}
