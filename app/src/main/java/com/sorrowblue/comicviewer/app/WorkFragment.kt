package com.sorrowblue.comicviewer.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.app.databinding.FragmentWorkBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import java.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class WorkFragment : FrameworkFragment(R.layout.fragment_work) {

    private val args: WorkFragmentArgs by navArgs()
    private val binding: FragmentWorkBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workInfo = WorkManager.getInstance(requireContext())
            .getWorkInfoByIdLiveData(UUID.fromString(args.uuid))
        viewLifecycleOwner.lifecycleScope.launch {
            workInfo.asFlow().collectLatest {
                when (it.state) {
                    WorkInfo.State.ENQUEUED -> Unit
                    WorkInfo.State.RUNNING -> {
                        binding.progress.isIndeterminate = true
                        binding.uuid.text = it.progress.getString("path")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        binding.progress.isIndeterminate = false
                        Toast.makeText(requireContext(), "完了しました", Toast.LENGTH_SHORT).show()

                    }
//                    WorkInfo.State.FAILED -> dismiss()
//                    WorkInfo.State.BLOCKED -> dismiss()
                    WorkInfo.State.CANCELLED -> {
                        binding.progress.isIndeterminate = true
                        Toast.makeText(requireContext(), "キャンセルしました", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    else -> {}
                }
            }
        }
    }
}
