package com.sorrowblue.comicviewer.library.onedrive.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.databinding.GoogledriveFragmentListBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

internal class OneDriveListFragment :
    PagingFragment<File>(com.sorrowblue.comicviewer.library.R.layout.googledrive_fragment_list) {

    private val binding: GoogledriveFragmentListBinding by viewBinding()
    override val viewModel: OneDriveListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarConfiguration = AppBarConfiguration(setOf())
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
        binding.signOut.setOnClickListener {
            viewModel.signOut()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAuthenticated.collectLatest {
                if (it == false) {
                    findNavController().navigate(OneDriveListFragmentDirections.actionOneDriveListToOneDriveSignin())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUserFlow.collectLatest {
                if (it != null) {
                    binding.displayName.text = it.displayName
                    binding.profilePhoto.load(viewModel.profileImage())
                } else {
                    binding.displayName.text = "no signed"
                    binding.profilePhoto.setImageResource(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_broken_image_24)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRefreshingFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    binding.progressIndicator.isVisible = true
                }
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

    override val adapter
        get() = OneDriveListAdapter {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_TITLE, it.name)
            file = it
            createFileRequest.launch(intent)
        }

    private lateinit var file: File

    private val createFileRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                logcat { "driveId=${file.params["driveId"]}, itemId=${file.path}" }
                val request = OneTimeWorkRequestBuilder<OneDriveDownloadWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        workDataOf(
                            "outputUri" to it.data!!.data!!.toString(),
                            "driveId" to file.params["driveId"],
                            "itemId" to file.path
                        )
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresStorageNotLow(true)
                            .build()
                    )
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(request)
            }
        }
}
