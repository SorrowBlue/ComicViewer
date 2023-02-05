package com.sorrowblue.comicviewer.folder

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.folder.databinding.FolderViewScanInfoBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FolderScanInfoDialog : DialogFragment() {

    private val binding: FolderViewScanInfoBinding by dialogViewBinding()
    private val args: FolderScanInfoDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val workInfo = WorkManager.getInstance(requireContext())
            .getWorkInfoByIdLiveData(UUID.fromString(args.uuid))
        findNavController().currentBackStackEntry?.lifecycleScope?.launch {
            var max = 0
            workInfo.asFlow().collectLatest {
                when (it.state) {
                    WorkInfo.State.ENQUEUED -> Unit
                    WorkInfo.State.RUNNING -> {
                        binding.path.text = it.progress.getString("path")
                        if (max != it.progress.getInt("max", 0)) {
                            max = it.progress.getInt("max", 0)
                            binding.progressIndicator.max = max
                            binding.progressIndicator.isIndeterminate = false
                        }
                        binding.progressIndicator.progress = it.progress.getInt("progress", 0)
                    }

                    WorkInfo.State.SUCCEEDED -> dismiss()
//                    WorkInfo.State.FAILED -> dismiss()
//                    WorkInfo.State.BLOCKED -> dismiss()
                    WorkInfo.State.CANCELLED -> {
                        Toast.makeText(requireContext(), "キャンセルしました", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }

                    else -> {}
                }
            }
        }
        val builder = MaterialAlertDialogBuilder(requireContext(), theme)
        builder.setTitle("スキャン")
        builder.setView(binding.root)
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            WorkManager.getInstance(requireContext())
                .cancelWorkById(UUID.fromString(args.uuid))
        }
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workInfo = WorkManager.getInstance(requireContext())
            .getWorkInfoByIdLiveData(UUID.fromString(args.uuid))
        workInfo.observe(findNavController().currentBackStackEntry!!) {
            binding.path.text = it.progress.getString("path")
        }
    }
}
