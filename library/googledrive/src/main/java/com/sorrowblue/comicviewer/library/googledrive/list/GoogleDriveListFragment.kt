package com.sorrowblue.comicviewer.library.googledrive.list

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.library.filelist.LibraryFileListFragment
import com.sorrowblue.comicviewer.library.googledrive.DriveDownloadWorker
import kotlinx.coroutines.flow.onEach
import logcat.logcat

internal class GoogleDriveListFragment : LibraryFileListFragment<GoogleDriveListAdapter>() {

    override val viewModel: GoogleDriveListViewModel by navGraphViewModels(com.sorrowblue.comicviewer.library.R.id.googledrive) {
        GoogleDriveListViewModel.Factory(GoogleDriveApiRepositoryImpl(requireContext()))
    }

    override fun onCreatePagingDataAdapter(): GoogleDriveListAdapter {
        return GoogleDriveListAdapter(::createFile) {
            findNavController().navigate(
                GoogleDriveListFragmentDirections.actionGoogledriveListSelf(it.path)
            )
        }
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
        viewModel.googleSignInAccount.onEach {
            logcat { "photoUri=${it?.photoUrl}" }
            profileImage.load(it?.photoUrl) {
//                error(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_broken_image_24)
            }
        }.launchInWithLifecycle()
    }

    override fun navigateToSignIn() {
        findNavController().navigate(GoogleDriveListFragmentDirections.actionGoogledriveListToGoogledriveSignin())
    }

    override fun navigateToProfile() {
        findNavController().navigate(GoogleDriveListFragmentDirections.actionGoogledriveListToGoogledriveProfile())
    }

    override fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "fileId" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)
    }
}
