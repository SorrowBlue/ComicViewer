package com.sorrowblue.comicviewer.folder

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.PagingException
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.folder.databinding.FolderFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.flow.attachAdapter
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class FolderFragment : PagingFragment<File>(R.layout.folder_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: FolderFragmentBinding by viewBinding()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override val viewModel: FolderViewModel by viewModels()
    override val adapter
        get() = FolderAdapter(
            runBlocking { viewModel.folderDisplaySettingsFlow.first().display },
            { file, transitionName, extras ->
                when (file) {
                    is Book -> navigate(
                        FolderFragmentDirections.actionFolderToBook(
                            file,
                            transitionName
                        ), extras
                    )

                    is Folder -> navigate(
                        FolderFragmentDirections.actionFolderSelf(
                            file.bookshelfId.value,
                            file.path.encodeBase64(),
                            transitionName
                        ), extras
                    )
                }
            },
            { navigate("comicviewer://comicviewer.sorrowblue.com/file_info?server_id=${it.bookshelfId.value}&path=${it.path.encodeBase64()}".toUri()) }
        )

    override fun onCreateAdapter(adapter: PagingDataAdapter<File, *>) {
        super.onCreateAdapter(adapter)
        check(adapter is FolderAdapter)
        viewModel.folderDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()
        viewModel.spanCountFlow.onEach(binding.frameworkUiRecyclerView::setSpanCount)
            .launchInWithLifecycle()
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.mapNotNull { it.refresh as? LoadState.Error }
                .distinctUntilChanged()
                .collectLatest {
                    if (it.error is PagingException) {
                        Snackbar.make(
                            binding.root,
                            (it.error as PagingException).getMessage(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        if (viewModel.position >= 0) {
            val position = viewModel.position
            viewModel.position = -1
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .first { it.refresh is LoadState.NotLoading && adapter.itemCount > 0 }
                    binding.frameworkUiRecyclerView.scrollToPosition(position)
                    commonViewModel.isRestored.emit(true)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController()
        binding.toolbar.setOnLongClickListener {
            findNavController().popBackStack(R.id.folder_navigation, true)
            true
        }
        binding.toolbar.setOnMenuItemClickListener(this)
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
        setupSearchAdapter()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.folder_search -> {
                binding.searchView.show()
                true
            }

            R.id.folder_refresh -> {
                scanType = ScanType.QUICK
                requestScan()
                true
            }

            R.id.folder_refresh_full -> {
                scanType = ScanType.FULL
                requestScan()
                true
            }

            else -> item.onNavDestinationSelected(findNavController())
        }
    }

    private fun FolderFragmentDirections.Companion.actionFolderToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionFolderToBook().actionId
        override val arguments = BookFragmentArgs(
            book.bookshelfId.value,
            book.path.encodeBase64(),
            transitionName,
            book.lastPageRead
        ).toBundle()
    }

    private fun setupSearchAdapter() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN
        ) {
            if (binding.searchView.isShown) {
                binding.searchView.hide()
            }
        }
        binding.searchView.addTransitionListener { _, _, newState ->
            callback.isEnabled =
                newState == SearchView.TransitionState.SHOWING || newState == SearchView.TransitionState.SHOWN
        }
        val adapter = FolderAdapter(
            FolderDisplaySettings.Display.LIST,
            { file, transitionName, extras ->
                when (file) {
                    is Book -> navigate(
                        FolderFragmentDirections.actionFolderToBook(file, transitionName),
                        extras
                    )

                    is Folder -> navigate(
                        FolderFragmentDirections.actionFolderSelf(
                            file.bookshelfId.value,
                            file.path.encodeBase64(),
                            transitionName
                        ), extras
                    )
                }
            },
            { navigate("comicviewer://comicviewer.sorrowblue.com/file_info?server_id=${it.bookshelfId.value}&path=${it.path.encodeBase64()}".toUri()) }
        )
        viewModel.pagingQueryDataFlow.attachAdapter(adapter)
        binding.searchRecyclerView.adapter = adapter
        binding.searchView.editText.doAfterTextChanged { editable ->
            editable?.toString()?.let {
                if (viewModel.query != it) {
                    viewModel.query = it
                    adapter.refresh()
                }
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            scanStart()
        } else {
            show()
        }
    }

    lateinit var scanType: ScanType

    private fun show() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("権限リクエスト")
            .setMessage("スキャン状況を表示するため通知を許可してください").setPositiveButton("許可する") { _, _ ->
                kotlin.runCatching {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            "package:${requireContext().packageName}".toUri()
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                }
            }.setNegativeButton("キャンセル") { _, _ ->
            }.setNeutralButton("許可しないで続行") { _, _ ->
                scanStart()
            }.show()
    }

    private fun requestScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestScanImpl33()
        } else {
            scanStart()
        }
    }

    private fun scanStart() {
        viewModel.fullScan(scanType) {
            NavDeepLinkBuilder(requireContext()).setGraph(findNavController().graph).setDestination(
                R.id.folder_scan_info_dialog,
                FolderScanInfoDialogArgs(it).toBundle()
            ).createPendingIntent()
            findNavController().navigate(FolderFragmentDirections.actionFolderToFolderScanInfo(it))
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestScanImpl33() {
        when {
            PermissionChecker.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_GRANTED -> {
                // パーミッションが許可済み
                scanStart()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // パーミッションが許可されていない禁止されている。
                // ユーザが許可しなかった場合。
                Snackbar.make(binding.root, "通知を許可してください", Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }.show()
            }

            else -> {
                // パーミッションが許可されていない禁止されている。
                // ユーザが許可しなかった場合（今後表示しないしない）。
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

private fun PagingException.getMessage(): String {
    return when (this) {
        PagingException.NoNetwork -> "ネットワークに接続していません。"
        PagingException.InvalidAuth -> "無効な認証情報"
        PagingException.InvalidServer -> "サーバーが見つかりません"
        PagingException.NotFound -> "ファイル/フォルダが見つかりません。"
    }
}
