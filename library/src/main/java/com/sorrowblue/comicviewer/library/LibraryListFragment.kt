package com.sorrowblue.comicviewer.library

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.databinding.LibraryFragmentListBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class LibraryListFragment : FrameworkFragment(R.layout.library_fragment_list) {

    private val binding: LibraryFragmentListBinding by viewBinding()
    private val viewModel: LibraryListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                is CloudStorage.GoogleDrive -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToGoogledriveNavigation(
                        extras.sharedElements.values.first()
                    ), extras
                )

                is CloudStorage.Box -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToBoxNavigation(extras.sharedElements.values.first()),
                    extras
                )

                is CloudStorage.Dropbox -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToDropboxNavigation(extras.sharedElements.values.first()),
                    extras
                )

                is CloudStorage.OneDrive -> navigate(
                    LibraryListFragmentDirections.actionLibraryListToOnedriveNavigation(extras.sharedElements.values.first()),
                    extras
                )
            }
        }
        adapter.submitList(LocalFeature.values().asList() + viewModel.cloudStorageList)
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

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToBoxNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToBoxNavigation().actionId
        override val arguments = bundleOf("transitionName" to transitionName)
    }

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToDropboxNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToDropboxNavigation().actionId
        override val arguments = bundleOf("transitionName" to transitionName)
    }

    private fun LibraryListFragmentDirections.Companion.actionLibraryListToOnedriveNavigation(
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionLibraryListToOnedriveNavigation().actionId
        override val arguments = bundleOf("transitionName" to transitionName)
    }
}
