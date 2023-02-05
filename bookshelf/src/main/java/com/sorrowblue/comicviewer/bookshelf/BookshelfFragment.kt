package com.sorrowblue.comicviewer.bookshelf

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentBinding
import com.sorrowblue.comicviewer.domain.PagingException
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.file.info.FileInfoFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.fragment.launchIn
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
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
internal class BookshelfFragment : PagingFragment<File>(R.layout.bookshelf_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: BookshelfFragmentBinding by viewBinding()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override val viewModel: BookshelfViewModel by viewModels()
    override val recyclerView get() = binding.recyclerView
    override val adapter get() = BookshelfAdapter(
        runBlocking { viewModel.folderDisplaySettingsFlow.first().display },
        { file, transitionName, extras ->
            when (file) {
                is Book -> navigate(
                    BookshelfFragmentDirections.actionBookshelfToBook(
                        file,
                        transitionName
                    ), extras
                )

                is Folder -> navigate(
                    BookshelfFragmentDirections.actionBookshelfSelf(
                        file.serverId.value,
                        file.path.encodeBase64(),
                        transitionName
                    ), extras
                )
            }
        },
        { navigate(BookshelfFragmentDirections.actionBookshelfToFileInfo(it)) }
    )

    override fun onCreateAdapter(adapter: PagingDataAdapter<File, *>) {
        super.onCreateAdapter(adapter)
        check(adapter is BookshelfAdapter)
        viewModel.folderDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()
        viewModel.spanCountFlow.onEach(binding.recyclerView::setSpanCount)
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
                    binding.recyclerView.scrollToPosition(position)
                    commonViewModel.isRestored.emit(true)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnLongClickListener {
            findNavController().popBackStack(R.id.bookshelf_navigation, true)
            true
        }
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
        setupSearchAdapter()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookchelf_search -> {
                binding.searchView.show()
                true
            }

            R.id.bookshelf_refresh -> {
                scanType = ScanType.QUICK
                requestScan()
                true
            }

            R.id.bookshelf_refresh_full -> {
                scanType = ScanType.FULL
                requestScan()
                true
            }

            else -> item.onNavDestinationSelected(findNavController())
        }
    }

    private fun BookshelfFragmentDirections.Companion.actionBookshelfToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionBookshelfToBook().actionId
        override val arguments = BookFragmentArgs(
            book.serverId.value,
            book.path.encodeBase64(),
            transitionName,
            book.lastPageRead
        ).toBundle()
    }

    private fun BookshelfFragmentDirections.Companion.actionBookshelfToFileInfo(file: File) =
        object : NavDirections {
            override val actionId = actionBookshelfToFileInfo().actionId
            override val arguments =
                FileInfoFragmentArgs(file.serverId.value, file.path.encodeBase64()).toBundle()
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
        val adapter = BookshelfAdapter(
            FolderDisplaySettings.Display.LIST,
            { file, transitionName, extras ->
                when (file) {
                    is Book -> navigate(
                        BookshelfFragmentDirections.actionBookshelfToBook(
                            file,
                            transitionName
                        ), extras
                    )

                    is Folder -> navigate(
                        BookshelfFragmentDirections.actionBookshelfSelf(
                            file.serverId.value,
                            file.path.encodeBase64(),
                            transitionName
                        ), extras
                    )
                }
            },
            { navigate(BookshelfFragmentDirections.actionBookshelfToFileInfo(it)) }
        )
        binding.recyclerView.setSpanCount(1)
        binding.searchRecyclerView.adapter = adapter
        binding.searchView.editText.doAfterTextChanged { editable ->
            editable?.toString()?.let {
                if (viewModel.query != it) {
                    viewModel.query = it
                    adapter.refresh()
                }
            }
        }

        viewModel.pagingQueryDataFlow.onEach {
            adapter.submitDataWithLifecycle(it)
        }.launchIn()
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
                R.id.bookshelf_scan_info_dialog,
                BookshelfScanInfoDialogArgs(it).toBundle()
            ).createPendingIntent()
            findNavController().navigate(BookshelfFragmentDirections.actionToBookshelfScanInfo(it))
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

context(Fragment)
fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.submitDataWithLifecycle(data: PagingData<T>) {
    submitData(viewLifecycleOwner.lifecycle, data)
}
