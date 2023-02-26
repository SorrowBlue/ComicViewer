package com.sorrowblue.comicviewer.library.box.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.databinding.LibraryFragmentCloudBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach

internal class BoxListFragment :
    PagingFragment<File>(com.sorrowblue.comicviewer.library.R.layout.library_fragment_cloud) {

    private val binding: LibraryFragmentCloudBinding by viewBinding()
    override val viewModel: BoxListViewModel by viewModels()

    override val adapter
        get() = BoxListAdapter {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_TITLE, it.name)
            intent.type = "*/*"
            file = it
            createFileRequest.launch(intent)
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
        viewModel.isAuthenticated().distinctUntilChanged().filterNot { it }.onEach {
            findNavController().navigate(BoxListFragmentDirections.actionBoxListToBoxSignin())
        }.launchInWithLifecycle()
    }

    private lateinit var file: File

    private val createFileRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
            }
        }
}
