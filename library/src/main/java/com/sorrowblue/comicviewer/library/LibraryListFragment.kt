package com.sorrowblue.comicviewer.library

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavDirections
import com.google.android.play.core.ktx.hasTerminalStatus
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.databinding.LibraryFragmentListBinding
import com.sorrowblue.comicviewer.library.dropbox.list.DropBoxListFragmentArgs
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.chrisbanes.insetter.applyInsetter
import logcat.logcat

@AndroidEntryPoint
internal class LibraryListFragment : FrameworkFragment(R.layout.library_fragment_list) {

    private val binding: LibraryFragmentListBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = SplitInstallManagerFactory.create(requireContext().applicationContext)

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        val adapter = LibraryListAdapter { library, extras ->
            when (library) {
                LocalFeature.DOWNLOADED -> {}

                CloudStorage.GOOGLE_DRIVE -> {
                    if (!manager.installedModules.contains("googledrive")) {
                        logcat { "ダウンロードが必要" }
                    }
                    navigate(
                        LibraryListFragmentDirections.actionLibraryListToGoogledriveNavigation(
                            extras.sharedElements.values.first()
                        ), extras
                    )
                }

                CloudStorage.BOX -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToBoxNavigation(),
                    extras
                )

                CloudStorage.DROP_BOX -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToDropboxNavigation(extras.sharedElements.values.first()),
                    extras
                )

                CloudStorage.ONE_DRIVE -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToOnedriveNavigation(extras.sharedElements.values.first()),
                    extras
                )
            }
        }
        adapter.submitList(LocalFeature.values().asList() + CloudStorage.values().asList())
        binding.frameworkUiRecyclerView.adapter = adapter
        binding.frameworkUiRecyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
    }

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToGoogledriveNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToGoogledriveNavigation().actionId
        override val arguments = bundleOf("transitionName" to transitionName)
    }

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToDropboxNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToDropboxNavigation().actionId
        override val arguments = DropBoxListFragmentArgs(transitionName = transitionName).toBundle()
    }

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToOnedriveNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToOnedriveNavigation().actionId
        override val arguments = bundleOf("transitionName" to transitionName)
    }
}
