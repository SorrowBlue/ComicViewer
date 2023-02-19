package com.sorrowblue.comicviewer.settings.security

import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.settings.security.password.PasswordManageState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class SettingsSecurityFragment :
    FrameworkPreferenceFragment(R.xml.settings_security_preference) {

    private val binding: SettingsSecurityBinding by preferenceBinding()
    private val viewModel: SettingsSecurityFragmentViewModel by hiltNavGraphViewModels(R.id.settings_security_navigation)
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.useBiometric.isVisible =
            BiometricManager.from(requireContext()).check({ true }, { true }, { false })
        binding.useBiometric.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            if (newValue) {
                BiometricManager.from(requireContext()).check(
                    onSuccess = { viewModel.updateUseBiometrics(true) },
                    noneEnrolled = { findNavController().navigate(SettingsSecurityFragmentDirections.actionSettingsSecurityToDeviceAuthRequest()) },
                    notSupported = { commonViewModel.snackbarMessage.tryEmit("この端末では生体認証は使用できません。($it)") }
                )
            } else {
                viewModel.updateUseBiometrics(false)
            }
            false
        }
        binding.auth.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            if (newValue) {
                findNavController().navigate(SettingsSecurityFragmentDirections.actionSettingsSecurityToSettingsSecurityManagePassword(
                    PasswordManageState.NEW.name))
            } else {
                findNavController().navigate(SettingsSecurityFragmentDirections.actionSettingsSecurityToSettingsSecurityManagePassword(
                    PasswordManageState.DELETE.name))
            }
            false
        }
        binding.password.setOnPreferenceClickListener {
            findNavController().navigate(SettingsSecurityFragmentDirections.actionSettingsSecurityToSettingsSecurityManagePassword(
                PasswordManageState.CHANGE.name))
            true
        }
        viewModel.securitySettingsFlow.map { it.useBiometrics }.distinctUntilChanged()
            .onEach(binding.useBiometric::setChecked)
            .launchInWithLifecycle()
        viewModel.securitySettingsFlow.map { it.password }.distinctUntilChanged()
            .onEach {
                binding.auth.isChecked = it != null
                binding.useBiometric.isEnabled = binding.auth.isChecked
                binding.password.isEnabled = binding.auth.isChecked
            }
            .launchInWithLifecycle()
    }
}
