package com.sorrowblue.comicviewer.library.box.list

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.library.filelist.LibraryFileListFragment
import kotlinx.coroutines.flow.onEach
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

internal class BoxListFragment : LibraryFileListFragment() {

    private val boxApiViewModel: BoxApiViewModel by navGraphViewModels(com.sorrowblue.comicviewer.library.R.id.box_navigation)

    override val viewModel: BoxListViewModel by viewModels {
        BoxListViewModel.Factory(boxApiViewModel.repository)
    }
    override val adapter
        get() = BoxListAdapter(::createFile) {
            findNavController().navigate(BoxListFragmentDirections.actionBoxListSelf(it.path))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notificationManager = NotificationManagerCompat.from(requireContext())
        val name = "ダウンロード状況"
        val descriptionText = "ダウンロード状況を表示します"
        val channel = NotificationChannelCompat.Builder(
            ChannelID.DOWNLOAD.id,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(name).setDescription(descriptionText)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userInfoFlow.onEach {
            profileImage.load("https://api.box.com/2.0/users/${it?.id}/avatar") {
                addHeader("Authorization", "Bearer ${viewModel.accessToken()}")
                error(FrameworkResourceR.drawable.ic_twotone_broken_image_24)
            }
        }.launchInWithLifecycle()
    }

    override fun navigateToProfile() {
        findNavController().navigate(BoxListFragmentDirections.actionBoxListToBoxProfile())
    }

    override fun navigateToSignIn() {
        findNavController().navigate(BoxListFragmentDirections.actionBoxListToBoxSignin())
    }

    override fun enqueueDownload(outputUri: String, file: File) {
        TODO("Not yet implemented")
    }
}
