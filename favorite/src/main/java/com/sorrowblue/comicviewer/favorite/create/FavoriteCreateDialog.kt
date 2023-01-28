package com.sorrowblue.comicviewer.favorite.create

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteViewCreateBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkDialogFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithDialogLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class FavoriteCreateDialog : FrameworkDialogFragment() {

    private val binding: FavoriteViewCreateBinding by dialogViewBinding()
    private val viewModel: FavoriteCreateViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?, builder: MaterialAlertDialogBuilder) {
        binding.viewModel = viewModel
        builder.setTitle(R.string.favorite_create_title_new_favorite)
        builder.setView(binding.root)
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(R.string.favorite_create_btn_create, null)
    }

    override fun onShow(dialog: AlertDialog) {
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            viewModel.create { dismiss() }
        }
        viewModel.title.map(String::isNotBlank)
            .onEach(positiveButton::setEnabled)
            .launchInWithDialogLifecycle()
    }
}
