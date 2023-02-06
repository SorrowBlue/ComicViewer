package com.sorrowblue.comicviewer.bookshelf.remove

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.bookshelf.info.R
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfRemoveConfirmDialog : FrameworkDialogFragment() {

    private val viewModel: BookshelfRemoveConfirmViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?, builder: MaterialAlertDialogBuilder) {
        with(builder) {
            setTitle("削除の確認")
            setMessage("削除しますか？")
            setPositiveButton("削除") { _, _ -> }
            setNegativeButton("いいえ") { _, _ -> }
        }
    }

    override fun onShow(dialog: AlertDialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            viewModel.remove {
                findNavController().popBackStack(R.id.bookshelf_info_fragment, true)
            }
        }
    }
}
