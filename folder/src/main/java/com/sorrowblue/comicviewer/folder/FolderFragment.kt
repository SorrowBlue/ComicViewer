package com.sorrowblue.comicviewer.folder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.paging.LoadState
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.PagingException
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.file.info.FileInfoNavigation
import com.sorrowblue.comicviewer.file.info.observeOpenFolder
import com.sorrowblue.comicviewer.file.list.FileListAdapter
import com.sorrowblue.comicviewer.file.list.FileListFragment
import com.sorrowblue.comicviewer.folder.databinding.FolderFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.flow.attachAdapter
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.autoClearedValue
import com.sorrowblue.comicviewer.framework.ui.fragment.checkSelfPermission
import com.sorrowblue.comicviewer.framework.ui.fragment.makeSnackbar
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.fragment.windowInsetsControllerCompat
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResultListener
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.logcat

@AndroidEntryPoint
internal class FolderFragment : FileListFragment(R.layout.folder_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: FolderFragmentBinding by viewBinding()
    private val commonViewModel: CommonViewModel by activityViewModels()
    override val viewModel: FolderViewModel by viewModels()

    private val bottomSheetBehavior by autoClearedValue { BottomSheetBehavior.from(binding.folderSearchView.contentRoot) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        var actionMode: ActionMode? = null
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this, false) {
            actionMode?.finish()
        }

        viewModel.isEditing.onEach { isEditing ->
            (binding.recyclerView.adapter as? FileListAdapter)?.isEditing = isEditing
            if (isEditing) {
                actionMode = binding.toolbar.startActionMode(object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                        MenuInflater(requireContext()).inflate(R.menu.folder_action, menu)
                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                        callback.isEnabled = true
                        return false
                    }

                    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) =
                        when (item.itemId) {
                            R.id.folder_menu_download_selected -> {
                                true
                            }

                            R.id.folder_menu_delete_selected -> {
                                viewModel.deleteHistoryBook((binding.recyclerView.adapter as? FileListAdapter)?.selectedItemIds.orEmpty())
                                true
                            }

                            R.id.folder_menu_add_read_mark -> {
                                true
                            }

                            R.id.folder_menu_remove_read_mark -> {
                                true
                            }

                            else -> false
                        }

                    override fun onDestroyActionMode(mode: ActionMode) {
                        viewModel.isEditing.value = false
                        (binding.recyclerView.adapter as? FileListAdapter)?.selectedItemIds?.clear()
                        (binding.recyclerView.adapter as? FileListAdapter)?.let {
                            it.notifyItemRangeChanged(0, it.itemCount)
                        }
                        callback.isEnabled = false
                        actionMode = null
                    }
                })
            }
        }.launchInWithLifecycle()


        binding.toolbar.setOnLongClickListener {
            findNavController().popBackStack(R.id.folder_navigation, true)
            true
        }
        binding.toolbar.setOnMenuItemClickListener(this)
        setupSearchAdapter()
        binding.folderSearchView.searchRecyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true, ime = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        observeOpenFolder(R.id.folder_fragment) { id, parent ->
            findNavController().navigate(FolderFragmentDirections.actionFolderSelf(id, parent))
        }

        setDialogFragmentResultListener<Boolean>(R.id.folder_fragment, "result") {
            if (it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // 直接通知の設定画面に遷移する
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.let(::startActivity)
                }
            } else {
                logcat { "通知は表示されません。" }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sortTypeFlow.collectLatest {
                pagingDataAdapter.refresh()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            pagingDataAdapter.loadStateFlow.mapNotNull { it.refresh as? LoadState.Error }
                .distinctUntilChanged().collectLatest {
                    if (it.error is PagingException) {
                        Snackbar.make(
                            binding.root,
                            (it.error as PagingException).getMessage(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        if (0 < viewModel.position) {
            val position = viewModel.position
            viewModel.position = -1
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    pagingDataAdapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .first { it.refresh is LoadState.NotLoading && pagingDataAdapter.itemCount > 0 }
                    binding.recyclerView.scrollToPosition(position)
                    commonViewModel.isRestored.emit(true)
                }
            }
        }
    }

    override fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    ) {
        when (file) {
            is Book -> findNavController().navigate(
                FolderFragmentDirections.actionFolderToBook(file, transitionName), extras
            )

            is Folder -> findNavController().navigate(
                FolderFragmentDirections.actionFolderSelf(
                    file.bookshelfId.value, file.base64Path(), transitionName
                ), extras
            )
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.folder_search -> {
                binding.searchView.show()
                true
            }

            R.id.folder_menu_scan -> {
                scan(ScanType.QUICK)
                true
            }
            R.id.folder_menu_full_scan -> {
                scan(ScanType.FULL)
                true
            }

            R.id.folder_menu_edit -> {
                viewModel.isEditing.value = true
                true
            }

            else -> item.onNavDestinationSelected(findNavController())
        }.also {
            logcat { "onMenuItemClick(${item.title})=$it" }
        }
    }

    private fun FolderFragmentDirections.Companion.actionFolderToBook(
        book: Book, transitionName: String
    ) = object : NavDirections {
        override val actionId = actionFolderToBook().actionId
        override val arguments = BookFragmentArgs(book, transitionName).toBundle()
    }

    private fun switchSearchResultSheet(state: Int? = null) {
        if ((state ?: bottomSheetBehavior.state) == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            WindowInsetsControllerCompat(requireActivity().window, requireView()).hide(
                WindowInsetsCompat.Type.ime()
            )
            val avd = AnimatedVectorDrawableCompat.create(
                requireContext(),
                com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_arrow_back_close
            )!!
            binding.searchView.toolbar.navigationIcon = avd
            avd.start()
            binding.searchView.toolbar.menu.findItem(R.id.folder_search_menu_filter).isVisible =
                false
            binding.folderSearchView.frontToolbar.menu.findItem(R.id.folder_search_menu_up).isVisible =
                true
            binding.folderSearchView.scrim.isVisible = true
        } else if ((state ?: bottomSheetBehavior.state) == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            val avd = AnimatedVectorDrawableCompat.create(
                requireContext(),
                com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_close_arrow_back
            )!!
            binding.searchView.toolbar.navigationIcon = avd
            avd.start()
            binding.searchView.toolbar.menu.findItem(R.id.folder_search_menu_filter).isVisible =
                true
            binding.folderSearchView.frontToolbar.menu.findItem(R.id.folder_search_menu_up).isVisible =
                false
            binding.folderSearchView.scrim.isVisible = false
        }
    }

    private fun setupSearchAdapter() {
        binding.folderSearchView.setViewModel(viewModel)
        val searchAdapter = FileListAdapter(FolderDisplaySettings.Display.LIST,
            runBlocking { viewModel.isEnabledThumbnailFlow.first() },
            { file, transitionName, extras ->
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.ime())
                navigateToFile(file, transitionName, extras)
            },
            {
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.ime())
                findNavController().navigate(FileInfoNavigation.getDeeplink(it))
            })

        binding.folderSearchView.searchRecyclerView.adapter = searchAdapter
        searchAdapter.loadStateFlow.distinctUntilChanged().onEach {
            binding.folderSearchView.frontToolbar.title = "${searchAdapter.itemCount} results"
        }.launchInWithLifecycle()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchSortTypeFlow.collectLatest {
                searchAdapter.refresh()
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN
        ) {
            if (binding.searchView.isShown) {
                binding.searchView.hide()
            }
        }
        binding.folderSearchView.scrim.setOnClickListener { switchSearchResultSheet() }
        binding.folderSearchView.frontToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.folder_search_menu_up) {
                switchSearchResultSheet()
            }
            true
        }

        binding.searchView.toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                switchSearchResultSheet()
            } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                binding.searchView.hide()
            }
        }

        callback.isEnabled =
            binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN

        var job: Job? = null
        binding.searchView.addTransitionListener { _, previousState, newState ->
            if (previousState == SearchView.TransitionState.SHOWN && newState == SearchView.TransitionState.HIDING) {
                // Start hide
                commonViewModel.isVisibleBottomNav.tryEmit(true)
                job?.cancel()
                job = null
            } else if (previousState == SearchView.TransitionState.HIDDEN && newState == SearchView.TransitionState.SHOWING) {
                // Start show
                commonViewModel.isVisibleBottomNav.tryEmit(false)
                job?.cancel()
                job = viewModel.searchPagingDataFlow.attachAdapter(searchAdapter)
            }
            callback.isEnabled =
                newState == SearchView.TransitionState.SHOWING || newState == SearchView.TransitionState.SHOWN
        }
        if (binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN) {
            commonViewModel.isVisibleBottomNav.tryEmit(false)
            job?.cancel()
            job = viewModel.searchPagingDataFlow.attachAdapter(searchAdapter)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchQueryFlow.collectLatest {
                binding.folderSearchView.searchRecyclerView.scrollToPosition(0)
            }
        }
        binding.searchView.toolbar.inflateMenu(R.menu.folder_search)
        binding.searchView.toolbar.menu.findItem(R.id.folder_search_menu_filter)
            .setOnMenuItemClickListener {
                switchSearchResultSheet()
                true
            }
        switchSearchResultSheet(BottomSheetBehavior.STATE_COLLAPSED)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
//                viewModel.fullScan(scanType) {
//                    makeSnackbar("スキャン中", Snackbar.LENGTH_INDEFINITE).setAction("詳細") {
//                        findNavController().navigate("comicviewer://comicviewer.sorrowblue.com/work?uuid=0".toUri())
//                    }.show()
//                }
            }
        }

    private var isExplainNotificationPermission = true

    private fun scan(scanType: ScanType) {

        when {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                // 通知権限がある場合
                viewModel.fullScan(scanType) {
                    makeSnackbar("スキャン中", Snackbar.LENGTH_INDEFINITE).setAction("詳細") {
                        findNavController().navigate("comicviewer://comicviewer.sorrowblue.com/work?uuid=0".toUri())
                    }.show()
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // ユーザに通知権限が必要な理由を説明する
                findNavController().navigate(
                    FolderFragmentDirections.actionFolderToNotificationRequestDialogFragment().actionId,
                    bundleOf("request_key" to this::class.qualifiedName)
                )
            }

            isExplainNotificationPermission -> {
                // 通知権限がない場合
                findNavController().navigate(
                    FolderFragmentDirections.actionFolderToNotificationRequestDialogFragment().actionId,
                    bundleOf("request_key" to this::class.qualifiedName)
                )
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

}
