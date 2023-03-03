package com.sorrowblue.comicviewer.settings.feature

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.settings.feature.databinding.SettingsFeatureFragmentListBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class SettingsFeatureListFragment :
    FrameworkFragment(R.layout.settings_feature_fragment_list), SplitInstallStateUpdatedListener {

    private val binding: SettingsFeatureFragmentListBinding by viewBinding()
    private val viewModel: SettingsFeatureListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.frameworkUiRecyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        val adapter = FeatureListAdapter {
            logcat { "$it" }
            when (it.status) {
                InstallStatus.Cancelled -> viewModel.startInstall(it.feature.moduleName)
                is InstallStatus.Failed -> viewModel.startInstall(it.feature.moduleName)
                InstallStatus.Installed -> {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("機能の削除")
                        .setMessage("${getString(it.feature.title)}機能を削除しますか？\n機能話すぐに削除されずアプリが使用していない間に自動的に削除されます。")
                        .setPositiveButton("削除") { _, _ ->
                            Snackbar.make(binding.root, "${it.feature.title}機能の削除をリクエストしました。", Snackbar.LENGTH_SHORT).show()
                            viewModel.uninstall(it.feature.moduleName)
                        }
                        .setNegativeButton("Cancel") { _, _ -> }
                        .create().show()
                }

                InstallStatus.NotInstall -> viewModel.startInstall(it.feature.moduleName)
                is InstallStatus.Progress -> viewModel.cancel(it.sessionId)
            }
        }
        viewModel.splitInstallManager.registerListener(this)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stateList.collectLatest {
                adapter.submitList(it)
            }
        }
        binding.frameworkUiRecyclerView.adapter = adapter
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        state.moduleNames.forEach { moduleName ->
            viewModel.stateList.value = viewModel.stateList.value.map {
                if (it.feature.moduleName == moduleName) {
                    it.copy(status = InstallStatus.from(state))
                } else {
                    it
                }
            }

        }
    }
}
