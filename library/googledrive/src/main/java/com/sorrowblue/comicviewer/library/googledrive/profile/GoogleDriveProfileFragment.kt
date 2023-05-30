package com.sorrowblue.comicviewer.library.googledrive.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import coil.load
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithDialogLifecycle
import com.sorrowblue.comicviewer.library.googledrive.R
import com.sorrowblue.comicviewer.library.googledrive.databinding.GoogledriveFragmentProfileBinding
import com.sorrowblue.comicviewer.library.googledrive.list.GoogleDriveApiRepositoryImpl
import com.sorrowblue.comicviewer.library.googledrive.list.GoogleDriveListViewModel
import com.sorrowblue.jetpack.binding.viewBinding
import kotlinx.coroutines.flow.onEach

class GoogleDriveProfileFragment : DialogFragment(R.layout.googledrive_fragment_profile) {

    private val binding: GoogledriveFragmentProfileBinding by viewBinding()
    private val viewModel: GoogleDriveListViewModel by navGraphViewModels(com.sorrowblue.comicviewer.library.R.id.googledrive) {
        GoogleDriveListViewModel.Factory(
            GoogleDriveApiRepositoryImpl(requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.googleSignInAccount.onEach {
            binding.boxShapeableimageview.load(it?.photoUrl)
        }.launchInWithDialogLifecycle()
    }
}
