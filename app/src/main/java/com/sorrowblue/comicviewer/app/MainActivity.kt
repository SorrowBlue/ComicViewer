package com.sorrowblue.comicviewer.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.app.databinding.ActivityMainBinding
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val commonViewModel: CommonViewModel by viewModels()
    private val navController
        get() = binding.navHostFragmentActivityMain.getFragment<NavHostFragment>()
            .findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
        splashScreen.setKeepOnScreenCondition {
            commonViewModel.shouldKeepOnScreen
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        commonViewModel.snackbarMessage.onEach {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }.launchInWithLifecycle()

        // 初期化処理未実施の場合
        if (commonViewModel.shouldKeepOnScreen) {
            // リストア Skip/完了 を待つ
            lifecycleScope.launch {
                restoreNavigation()
                commonViewModel.isRestored.first()
                if (viewModel.securitySettingsFlow.first().password != null) {
                    navController.navigate(MobileNavigationDirections.actionGlobalAuthFragment())
                }
                commonViewModel.shouldKeepOnScreen = false
            }
        }
        binding.bottomNavigation.setupWithNavController(navController)
        commonViewModel.isVisibleBottomNavigation.onEach {
            binding.bottomNavigation.isVisible = it
        }.launchIn(lifecycleScope)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            commonViewModel.isVisibleBottomNavigation.value = true
            commonViewModel.isVisibleFab.value = false
        }
    }

    private suspend fun restoreNavigation() {
        if (!viewModel.settings.first().restoreOnLaunch) {
            logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Do not restore navigation." }
            commonViewModel.shouldKeepOnScreen = false
            commonViewModel.isRestored.emit(true)
            return
        }
        val (server, bookshelves, position) = viewModel.getNavigationHistory().first()?.triple
            ?: kotlin.run {
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "Do not restore navigation(GET_HISTORY_ERROR)."
                }
                commonViewModel.isRestored.emit(true)
                return
            }
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Start restore navigation." }
        if (bookshelves.isNotEmpty()) {
            // library -> bookshelf
            if (bookshelves.size == 1) {
                navController.navigate(
                    com.sorrowblue.comicviewer.server.R.id.action_server_list_to_folder,
                    FolderFragmentArgs(
                        server.id.value,
                        bookshelves.first().base64Path(),
                        position = position
                    ).toBundle()
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "server(${server.id}) -> bookshelf(${bookshelves.first().path})"
                }
            } else {
                navController.navigate(
                    com.sorrowblue.comicviewer.server.R.id.action_server_list_to_folder,
                    FolderFragmentArgs(
                        server.id.value,
                        bookshelves.first().base64Path(),
                        position = position
                    ).toBundle()
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "server(${server.id}) -> bookshelf(${bookshelves.first().path})"
                }
                bookshelves.drop(1).dropLast(1).forEachIndexed { index, bookshelf ->
                    // bookshelf -> bookshelf
                    navController.navigate(
                        com.sorrowblue.comicviewer.folder.R.id.action_folder_self,
                        FolderFragmentArgs(server.id.value, bookshelf.base64Path()).toBundle()
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelves[index].path}) -> bookshelf${bookshelves[index + 1].path}"
                    }
                }
                navController.navigate(
                    com.sorrowblue.comicviewer.folder.R.id.action_folder_self,
                    FolderFragmentArgs(
                        server.id.value,
                        bookshelves.last().base64Path(),
                        position = position
                    ).toBundle()
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${
                        bookshelves.dropLast(1).last().path
                    }) -> bookshelf${bookshelves.last().path}"
                }
            }
        } else {
            commonViewModel.isRestored.emit(true)
        }
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Done restore navigation." }
    }
}
