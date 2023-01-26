package com.sorrowblue.comicviewer.settings.security

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
internal class DeviceAuthRequestDialog : DialogFragment() {

    private val viewModel: SettingsSecurityFragmentViewModel by hiltNavGraphViewModels(R.id.settings_security_navigation)
    private val commonViewModel: CommonViewModel by activityViewModels()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val biometricManager = BiometricManager.from(requireContext())
            when (biometricManager.canAuthenticate(BiometricUtil.authenticators)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    viewModel.updateUseBiometrics(true)
                    commonViewModel.snackbarMessage.tryEmit("生体認証を有効にしました。")
                    dismiss()
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    commonViewModel.snackbarMessage.tryEmit("生体認証を設定してください。")

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    commonViewModel.snackbarMessage.tryEmit("この端末では生体認証は使用できません。")
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("端末の設定で品証を有効化してください")
            .setMessage("端末の設定で品証を有効化してください")
            .setPositiveButton("設定") { _, _ ->
            }
            .create().apply {
                setOnShowListener {
                    val positiveButton =
                        (it as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener {
                        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                )
                            }
                        } else {
                            Intent(Settings.ACTION_SECURITY_SETTINGS)
                        }
                        resultLauncher.launch(enrollIntent)
                    }
                }
            }
    }
}
