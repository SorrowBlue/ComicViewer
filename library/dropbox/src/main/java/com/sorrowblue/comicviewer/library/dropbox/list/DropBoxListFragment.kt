package com.sorrowblue.comicviewer.library.dropbox.list

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepositoryImpl
import com.sorrowblue.comicviewer.library.filelist.LibraryFileListFragment
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

internal class DropBoxListFragment : LibraryFileListFragment() {

    override val viewModel: DropBoxListViewModel by viewModels {
        DropBoxListViewModel.Factory(DropBoxApiRepositoryImpl(requireContext()))
    }

    override val adapter
        get() = DropBoxListAdapter(::createFile) {
            findNavController().navigate(DropBoxListFragmentDirections.actionDropboxListSelf(it.parent))
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
        viewModel.account.filterNotNull()
            .onEach { profileImage.load(it.profilePhotoUrl) }
            .launchInWithLifecycle()
    }

    override fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DropBoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)
    }

    override fun navigateToSignIn() {
        findNavController().navigate(DropBoxListFragmentDirections.actionDropboxListToDropboxSignin())
    }

    override fun navigateToProfile() {
        TODO("Not yet implemented")
    }
}
