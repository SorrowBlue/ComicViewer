package com.sorrowblue.comicviewer.framework.ui.fragment

import android.os.Bundle
import android.view.View
import com.sorrowblue.comicviewer.framework.ui.R
import com.sorrowblue.comicviewer.framework.ui.databinding.FrameworkUiFragmentProgressBinding
import com.sorrowblue.jetpack.binding.viewBinding
import kotlin.math.ceil

class FrameworkProgressFragment :
    FixedAbstractProgressFragment(R.layout.framework_ui_fragment_progress) {

    private val binding: FrameworkUiFragmentProgressBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun bindModule(modules: List<String>) {
        binding.module.text = modules.joinToString(",")
    }

    override fun onProgress(status: Int, bytesDownloaded: Long, bytesTotal: Long) {
        binding.progress.progress = ceil(bytesDownloaded.toDouble() / bytesTotal * 100).toInt()
    }

    override fun onCancelled() {
        binding.status.text = "cancelled"
    }

    override fun onFailed(errorCode: Int) {
        binding.status.text = "errorCode: $errorCode"
    }
}
