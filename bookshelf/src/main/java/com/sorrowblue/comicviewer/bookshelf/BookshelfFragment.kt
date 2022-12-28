package com.sorrowblue.comicviewer.bookshelf

import android.Manifest
import android.app.SearchManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.sorrowblue.comicviewer.domain.model.ScanType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfFragment : AbstractBookshelfFragment() {

    override val viewModel: BookshelfViewModel by viewModels()

    override val menuResId = R.menu.bookshelf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            scrimColor = MaterialColors.getColor(
                requireContext(),
                android.R.attr.colorBackground,
                Color.TRANSPARENT
            )
            setPathMotion(MaterialArcMotion())
        }
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView =
            binding.toolbar.menu.requireItem(R.id.bookchelf_search).requireActionView<SearchView>()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val server = viewModel.serverFlow.value ?: return false
                    navigate(BookshelfFragmentDirections.actionToSearchableBookshelf(server, query))
                    return true
                } else {
                    return false
                }

            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    private fun Menu.requireItem(id: Int) = findItem(id)

    private inline fun <reified T> MenuItem.requireActionView(): T {
        val view = actionView
        checkNotNull(view) {
            ("MenuItem " + this + " did not return a View from"
                    + " onCreateView() or this was called before onCreateView().")
        }
        check(actionView is T)
        return actionView as T
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
