package com.sorrowblue.comicviewer.app

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.DynamicColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.splitcompat.SplitCompat
import com.sorrowblue.comicviewer.app.databinding.ActivityMainBinding
import com.sorrowblue.comicviewer.app.ktx.isShown
import com.sorrowblue.comicviewer.app.ktx.setState
import com.sorrowblue.comicviewer.app.ktx.setupWithNavControllerApp
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val commonViewModel: CommonViewModel by viewModels()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            DynamicColors.applyToActivityIfAvailable(this@MainActivity)
            super.onCreate(savedInstanceState)
            setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
            setKeepOnScreenCondition(commonViewModel::shouldKeepOnScreen)
        }


        val navController =
            binding.navHostFragment.getFragment<NavHostFragment>().findNavController()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.viewModel = viewModel
        binding.commonViewModel = commonViewModel
        binding.bottomNavigation.setupWithNavControllerApp(navController)
        navController.addOnDestinationChangedListener(commonViewModel)
        setupAuthSheet()

        // 初期化処理未実施の場合
        if (commonViewModel.shouldKeepOnScreen) {
            // リストア Skip/完了 を待つ
            lifecycleScope.launch {
                restoreNavigation(navController)
                commonViewModel.isRestored.first()
                if (!runBlocking { viewModel.doneTutorialFlow.first() }) {
//                    navController.navigate(
//                        MobileNavigationDirections.actionGlobalTutorialNavigation().actionId,
//                        TutorialFragmentArgs(R.id.mobile_navigation).toBundle()
//                    )
                }
                commonViewModel.shouldKeepOnScreen = false
            }
        }

        commonViewModel.isVisibleBottomNav.onEach(binding.bottomNavigation::isShown)
            .launchInWithLifecycle()

        commonViewModel.fabState.onEach(binding.frameworkUiFab::setState).launchInWithLifecycle()

        commonViewModel.snackbarMessage.onEach {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).apply {
                anchorView = binding.frameworkUiFab
                isAnchorViewLayoutListenerEnabled = true
            }.show()
        }.launchInWithLifecycle()
    }

    override fun onStart() {
        super.onStart()
        if (runBlocking { viewModel.securitySettingsFlow.first() }.lockOnBackground) {
            viewModel.isShownAuthSheet.value = true
        }
    }

    private fun setupAuthSheet() {
        binding.viewAuth.password.doAfterTextChanged {
            binding.viewAuth.textInputLayout.error = null
            binding.viewAuth.textInputLayout.isErrorEnabled = false
        }
        val windowInsetsController = WindowInsetsControllerCompat(window, binding.viewAuth.root)
        viewModel.isShownAuthSheet.onEach { isShownAuthSheet ->
            if (isShownAuthSheet) {
                windowInsetsController.show(WindowInsetsCompat.Type.ime())
                binding.viewAuth.password.requestFocus()
            } else {
                windowInsetsController.hide(WindowInsetsCompat.Type.ime())
                binding.root.requestFocus()
            }
        }.launchInWithLifecycle()
        viewModel.authError.filter { it != 0 }.map(::getString)
            .onEach(binding.viewAuth.textInputLayout::setError).launchInWithLifecycle()
    }

    private suspend fun restoreNavigation(navController: NavController) {
        if (!viewModel.settings.first().restoreOnLaunch) {
            logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Do not restore navigation." }
            commonViewModel.isRestored.emit(true)
            return
        }
        val (bookshelf, folders, position) = viewModel.getNavigationHistory().first()?.triple
            ?: kotlin.run {
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "Do not restore navigation(GET_HISTORY_ERROR)."
                }
                commonViewModel.isRestored.emit(true)
                return
            }
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Start restore navigation." }
        if (folders.isNotEmpty()) {
            // library -> folder
            if (folders.size == 1) {
//                navController.navigate(
//                    com.sorrowblue.comicviewer.bookshelf.R.id.action_bookshelf_list_to_folder,
//                    FolderFragmentArgs(
//                        bookshelf.id.value, folders.first().base64Path(), position = position
//                    ).toBundle()
//                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelf.id}) -> folder(${folders.first().path})"
                }
            } else {
//                navController.navigate(
//                    com.sorrowblue.comicviewer.bookshelf.R.id.action_bookshelf_list_to_folder,
//                    FolderFragmentArgs(
//                        bookshelf.id.value, folders.first().base64Path(), position = position
//                    ).toBundle()
//                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelf.id}) -> folder(${folders.first().path})"
                }
                folders.drop(1).dropLast(1).forEachIndexed { index, folder ->
                    // folder -> folder
                    TODO()
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "folder(${folders[index].path}) -> folder${folders[index + 1].path}"
                    }
                }
                TODO()
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "folder(${
                        folders.dropLast(1).last().path
                    }) -> folder${folders.last().path}"
                }
            }
        } else {
            commonViewModel.isRestored.emit(true)
        }
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Done restore navigation." }
    }
}

@BindingAdapter("layout_behavior_expandedOffset")
fun ViewGroup.setLayoutBehaviorExpandedOffset(expandedOffset: Int) {
    BottomSheetBehavior.from(this).expandedOffset = expandedOffset
}

@BindingAdapter("layout_behavior_draggable")
fun ViewGroup.setLayoutBehaviorDraggable(isDraggable: Boolean) {
    BottomSheetBehavior.from(this).isDraggable = isDraggable
}

@BindingAdapter("layout_behavior_shown")
fun ViewGroup.setLayoutBehaviorShown(isShown: Boolean) {
    if (isShown) {
        BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
    } else {
        BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_HIDDEN
    }
}
