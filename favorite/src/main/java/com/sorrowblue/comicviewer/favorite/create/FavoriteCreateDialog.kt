package com.sorrowblue.comicviewer.favorite.create

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteViewCreateBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FavoriteCreateDialog : DialogFragment() {

    private val binding: FavoriteViewCreateBinding by viewBinding()
    private val viewModel: FavoriteCreateViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.viewModel = viewModel
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("新しいお気に入りリスト")
            .setView(binding.root)
            .setNegativeButton("キャンセル", null)
            .setPositiveButton("作成", null)
            .create().apply {
                setOnShowListener {
                    val positiveButton =
                        (it as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                    parentFragment?.viewLifecycleOwner?.lifecycleScope?.launch {
                        viewModel.title.collectLatest {
                            positiveButton.isEnabled = it.isNotBlank()
                        }
                    }
                    positiveButton.setOnClickListener {
                        if (viewModel.title.value.isBlank()) {
                            binding.title.error = "なにか入力してください。"
                        } else {
                            viewModel.create { dismiss() }
                        }
                    }
                }
            }
    }

}
