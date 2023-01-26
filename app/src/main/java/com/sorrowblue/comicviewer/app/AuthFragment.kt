package com.sorrowblue.comicviewer.app

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.app.databinding.FragmentAuthBinding
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.settings.security.BiometricUtil
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class AuthFragment : FrameworkFragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding()
    private val viewModel: AuthViewModel by viewModels()

    private var back = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (back) {
                requireActivity().finish()
            } else {
                Snackbar.make(
                    binding.root,
                    "アプリを終了させるにはもう一度戻るボタンを押してください。",
                    Snackbar.LENGTH_LONG
                )
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            back = false
                        }

                        override fun onShown(sb: Snackbar?) {
                            back = true
                        }
                    })
                    .show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.password.doAfterTextChanged {
            binding.textInputLayout.error = null
            binding.textInputLayout.isErrorEnabled = false
        }
        binding.password.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (binding.password.editableText.toString() == runBlocking { viewModel.settings.first() }.password) {
                    findNavController().navigateUp()
                    true
                } else {
                    binding.textInputLayout.error = "パスワードが間違っています。"
                    true
                }
            } else {
                false
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            if (viewModel.settings.first().useBiometrics) {
                performBiometricAuthentication()
            }
        }
    }

    private fun performBiometricAuthentication() {
        when (BiometricManager.from(requireContext()).canAuthenticate(BiometricUtil.authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                // 生体認証が利用できないため
                return
            }

            BiometricManager.BIOMETRIC_SUCCESS -> Unit
        }
        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireActivity()),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        requireActivity(),
                        androidx.biometric.R.string.fingerprint_not_recognized,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    ActivityCompat.finishAffinity(requireActivity())
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("生体認証")
            .setSubtitle("確認のため、生体認証を行ってください")
            .setConfirmationRequired(false)
            .setAllowedAuthenticators(BiometricUtil.authenticators)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}

@HiltViewModel
internal class AuthViewModel @Inject constructor(manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase) :
    ViewModel() {

    val settings = manageSecuritySettingsUseCase.settings
}
