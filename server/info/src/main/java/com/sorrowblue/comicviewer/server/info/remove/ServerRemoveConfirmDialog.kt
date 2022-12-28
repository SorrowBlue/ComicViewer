package com.sorrowblue.comicviewer.server.info.remove

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.server.info.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ServerRemoveConfirmDialog : DialogFragment() {

    private val viewModel: ServerRemoveConfirmViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("削除の確認")
            .setMessage("削除しますか？")
            .setPositiveButton("削除") { _, _ ->
                viewModel.remove()
                findNavController().popBackStack(R.id.server_info_fragment, true)
            }
            .setNegativeButton("いいえ") { _, _ -> }
            .create()
    }
}
