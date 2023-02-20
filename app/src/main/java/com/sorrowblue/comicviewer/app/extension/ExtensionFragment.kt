package com.sorrowblue.comicviewer.app.extension

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.sessionId
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.app.R
import com.sorrowblue.comicviewer.app.databinding.FragmentExtensionBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import kotlin.math.ceil
import logcat.logcat

class ExtensionFragment : FrameworkFragment(R.layout.fragment_extension) {

    private val binding: FragmentExtensionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val splitInstallManager = SplitInstallManagerFactory.create(requireContext())

        val request = SplitInstallRequest.newBuilder()
            .addModule("document")
            .build()
        var mySessionId = 0
        splitInstallManager.registerListener {
            if (it.sessionId == mySessionId) {
                val progress = it.bytesDownloaded.toDouble() / it.totalBytesToDownload * 100
                binding.progress.progress = ceil(progress).toInt()
                if (it.status == SplitInstallSessionStatus.INSTALLED) {
                    Snackbar.make(binding.root, it.moduleNames().first() +"のインストールが完了しました。", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.download.setOnClickListener {
            binding.progress.isIndeterminate = true
            splitInstallManager.startInstall(request)
                .addOnSuccessListener {
                    binding.progress.isIndeterminate = false
                    binding.progress.progress = 0
                    mySessionId = it
                    binding.state.text = "インストール成功"
                }
                .addOnFailureListener {
                    binding.state.text = "インストールエラー"
                }
        }

        kotlin.runCatching {
            val storageModuleProvider = Class.forName("com.sorrowblue.extention.document.PdfReader").getDeclaredConstructor()
            storageModuleProvider.newInstance()
        }.onFailure {
            logcat { "未インストール" }
        }
    }
}
