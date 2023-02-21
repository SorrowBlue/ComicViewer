package com.sorrowblue.comicviewer.library.googledrive.list

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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSrcCompat
import com.sorrowblue.comicviewer.library.R
import com.sorrowblue.comicviewer.library.databinding.GoogledriveFragmentListBinding
import com.sorrowblue.comicviewer.library.googledrive.DriveDownloadWorker
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


internal class GoogleDriveListFragment : PagingFragment<File>(R.layout.googledrive_fragment_list) {

    private val binding: GoogledriveFragmentListBinding by viewBinding()

    override val viewModel: GoogleDriveListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarConfiguration = AppBarConfiguration(setOf())


        viewModel.googleSignInAccount.onEach {
            binding.profilePhoto.setSrcCompat(it?.photoUrl)
            binding.displayName.text = it?.displayName
            binding.frameworkUiRecyclerView.isVisible = it != null
        }.launchInWithLifecycle()

        viewModel.isRefreshingFlow.onEach {
            binding.progressIndicator.isVisible = it
        }.launchInWithLifecycle()

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.googleSignInAccount.flowWithLifecycle(lifecycle).collectLatest {
                if (it == null) {
                    findNavController().navigate(GoogleDriveListFragmentDirections.actionGoogledriveListToGoogledriveSignin())
                }
            }
        }

        binding.signOut.setOnClickListener {
            val googleSignInClient =
                GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
            viewLifecycleOwner.lifecycleScope.launch {
                googleSignInClient.signOut().await()
                viewModel.updateAccount()
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
        get() = GoogleDriveListAdapter {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_TITLE, it.name)
            file = it
            createFileRequest.launch(intent)
        }

    lateinit var file: File

    private val createFileRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        workDataOf(
                            "outputUri" to it.data!!.data!!.toString(),
                            "fileId" to file.path
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
