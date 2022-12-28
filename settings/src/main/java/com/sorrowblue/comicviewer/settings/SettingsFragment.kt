package com.sorrowblue.comicviewer.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.framework.ui.BiometricUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class SettingsFragment : FrameworkPreferenceFragment(R.xml.settings_preference) {

    private val viewModel: SettingsViewModel by viewModels()
    private val binding: SettingsBinding by preferenceBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewerSettings.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsViewer())
            true
        }
        binding.displaySettings.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToDisplaySettings())
            true
        }

        binding.bookshelf.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsBookshelf())
            true
        }

        binding.restoreOnLaunch.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateRestoreOnLaunch(newValue)
            false
        }

        binding.useBiometric.isVisible = when (BiometricManager.from(requireContext())
            .canAuthenticate(BiometricUtil.authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }
        binding.useBiometric.setOnPreferenceChangeListener<Boolean> { _, t ->
            if (t) {
                val biometricManager = BiometricManager.from(requireContext())
                val result = biometricManager.canAuthenticate(BiometricUtil.authenticators)
                when (result) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        viewModel.updateUseAuth(true)
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        findNavController().navigate(SettingsFragmentDirections.settingsActionSettingsFragmentToSettingsAuthRequestDialog())
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                        Toast.makeText(requireContext(), "この端末では生体認証は使用できません。", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                logcat { "result=$result" }
            } else {
                viewModel.updateUseAuth(false)
            }
            false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settings.collectLatest {
                logcat { "settings=${it}" }
                binding.useBiometric.isChecked = it.useAuth
                binding.restoreOnLaunch.isChecked = it.restoreOnLaunch
            }
        }
    }
}
