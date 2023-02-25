package com.sorrowblue.comicviewer.app.extension

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.sessionId
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.app.R
import com.sorrowblue.comicviewer.app.databinding.FragmentExtensionBinding
import com.sorrowblue.comicviewer.app.databinding.ViewExtensionBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class ExtensionFragment : FrameworkFragment(R.layout.fragment_extension) {

    private val binding: FragmentExtensionBinding by viewBinding()
    private val manager by lazy { SplitInstallManagerFactory.create(requireContext().applicationContext) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.contentRoot.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        binding.dynamic.title.text = "dynamic"
        binding.document.title.text = "document"
        binding.googledrive.title.text = "googledrive"
        binding.onedrive.title.text = "onedrive"

        val modules = setOf(
            binding.dynamic to "dynamic",
            binding.document to "document",
            binding.googledrive to "googledrive",
            binding.onedrive to "onedrive"
        )
        modules.forEach {
            if (manager.installedModules.contains(it.second)) {
                log("init moduleName=${it.second}, status=INSTALLED")
                it.first.initUI(it.second, "INSTALLED")
            } else {
                log("init moduleName=${it.second}, status=NOT_INSTALL")
                it.first.initUI(it.second, "NOT_INSTALL")
            }
        }
    }

    private fun ViewExtensionBinding.updateUI(
        status: String,
        progress: Int
    ) {
        log("moduleName=${title.text}, status=$status")
        this.status.text = status
        this.progress.progress = progress
    }

    private fun ViewExtensionBinding.initUI(
        moduleName: String,
        status: String,
    ) {
        this.title.text = moduleName
        this.status.text = status
        this.progress.max = 100
        btn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val sessionId = manager.requestInstall(listOf(moduleName))
                log("moduleName=$moduleName, sessionId=$sessionId")
                manager.registerListener {
                    if (sessionId == it.sessionId) {
                        when (it.status) {
                            SplitInstallSessionStatus.CANCELED -> {
                                updateUI("CANCELED", 0)
                            }

                            SplitInstallSessionStatus.CANCELING -> {
                                updateUI("CANCELING", 0)
                            }

                            SplitInstallSessionStatus.DOWNLOADED -> {
                                updateUI("DOWNLOADED", 0)
                            }

                            SplitInstallSessionStatus.DOWNLOADING -> {
                                updateUI(
                                    "DOWNLOADING",
                                    (it.bytesDownloaded.toFloat() / it.totalBytesToDownload).toInt()
                                )
                            }

                            SplitInstallSessionStatus.FAILED -> {
                                updateUI("FAILED", 0)
                            }

                            SplitInstallSessionStatus.INSTALLED -> {
                                updateUI("INSTALLED", 0)
                            }

                            SplitInstallSessionStatus.INSTALLING -> {
                                updateUI("INSTALLING", 0)
                            }

                            SplitInstallSessionStatus.PENDING -> {
                                updateUI("PENDING", 0)
                            }

                            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                                updateUI("REQUIRES_USER_CONFIRMATION", 0)
                            }

                            SplitInstallSessionStatus.UNKNOWN -> {
                                updateUI("UNKNOWN", 0)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun log(str: String) {
        logcat { str }
        binding.log.text = binding.log.text.toString() + "\n" + str
    }
}
