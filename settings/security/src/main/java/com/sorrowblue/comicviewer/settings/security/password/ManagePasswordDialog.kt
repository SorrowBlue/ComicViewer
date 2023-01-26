package com.sorrowblue.comicviewer.settings.security.password

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.settings.security.R
import com.sorrowblue.comicviewer.settings.security.databinding.SettingsSecurityViewPasswordBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ManagePasswordDialog : DialogFragment() {

    private val binding: SettingsSecurityViewPasswordBinding by viewBinding { findNavController().currentBackStackEntry!! }
    private val viewModel: ManagePasswordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.viewModel = viewModel
        binding.beforePassword.doAfterTextChanged {
            binding.beforePasswordLayout.isErrorEnabled = false
        }
        binding.password.doAfterTextChanged {
            binding.passwordLayout.isErrorEnabled = false
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.state.titleRes)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .setView(binding.root)
            .create {
                when (viewModel.state) {
                    PasswordManageState.NEW -> viewModel.updatePassword(
                        null,
                        binding.password.text.toString()
                    ) { dismiss() }

                    PasswordManageState.CHANGE -> viewModel.updatePassword(
                        binding.beforePassword.text.toString(),
                        binding.password.text.toString()
                    ) {
                        if (it) {
                            dismiss()
                        } else {
                            binding.beforePasswordLayout.error =
                                getString(R.string.settings_security_password_manage_dialog_error_passwords_do_not_match)
                        }
                    }

                    PasswordManageState.DELETE -> viewModel.updatePassword(
                        binding.password.text.toString(),
                        null
                    ) {
                        if (it) {
                            dismiss()
                        } else {
                            binding.passwordLayout.error =
                                getString(R.string.settings_security_password_manage_dialog_error_passwords_do_not_match)
                        }
                    }
                }
            }
    }
}

private fun MaterialAlertDialogBuilder.create(onClickPositive: (() -> Unit)? = null): AlertDialog {
    return create().apply {
        if (onClickPositive != null) {
            setOnShowListener {
                (it as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                    onClickPositive.invoke()
                }
            }
        }
    }
}
