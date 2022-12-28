package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.app.Application
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.app.databinding.ActivityMainBinding
import com.sorrowblue.comicviewer.bookshelf.BookshelfFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.BiometricUtil
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.Inet4Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.logcat


@AndroidEntryPoint
internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val navController
        get() = binding.navHostFragmentActivityMain.getFragment<NavHostFragment>()
            .findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setOnExitAnimationListener { provider ->
            kotlin.runCatching {
                val slideUp = ObjectAnimator.ofFloat(
                    provider.view,
                    View.TRANSLATION_Y,
                    0f,
                    -provider.iconView.height * 2f
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.doOnEnd { provider.remove() }
                slideUp.duration =
                    if (provider.iconAnimationDurationMillis - System.currentTimeMillis() + provider.iconAnimationStartMillis < 0) 300 else provider.iconAnimationDurationMillis - System.currentTimeMillis() + provider.iconAnimationStartMillis
                slideUp.start()
            }.onFailure { provider.remove() }
        }
        splashScreen.setKeepOnScreenCondition {
            viewModel.shouldKeepOnScreen
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launch {
            val settings = viewModel.settings.first()
            if (settings.useAuth) {
                // 生体認証起動
                performBiometricAuthentication(settings.restoreOnLaunch)
            } else if (settings.restoreOnLaunch) {
                restoreNavigation()
            } else {
                viewModel.shouldKeepOnScreen = false
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun restoreNavigation() {
        lifecycleScope.launch {
            val (server, bookshelf, position) = viewModel.navigationHistory.first()
            viewModel.navigationHistory.resetReplayCache()
            if (server != null && bookshelf.isNotEmpty()) {
                logcat(
                    "RESTORE_NAVIGATION",
                    LogPriority.INFO
                ) { "Start restore navigation." }
                // library -> bookshelf
                navController.navigate(
                    com.sorrowblue.comicviewer.server.R.id.action_server_list_to_bookshelf_navigation,
                    BookshelfFragmentArgs(
                        server.id.value,
                        bookshelf.first().base64Path()
                    ).toBundle()
                )
                logcat(
                    "RESTORE_NAVIGATION",
                    LogPriority.INFO
                ) { "library(${server.id}) -> bookshelf(${bookshelf.first().path})" }
                bookshelf.drop(1).dropLast(1).forEachIndexed { index, it ->
                    // bookshelf -> bookshelf
                    navController.navigate(
                        com.sorrowblue.comicviewer.bookshelf.R.id.action_bookshelf_fragment_self,
                        BookshelfFragmentArgs(
                            server.id.value,
                            it.base64Path()
                        ).toBundle()
                    )
                    logcat(
                        "RESTORE_NAVIGATION",
                        LogPriority.INFO
                    ) { "bookshelf(${bookshelf[index].path}) -> bookshelf${bookshelf[index + 1].path}" }
                }
                navController.navigate(
                    com.sorrowblue.comicviewer.bookshelf.R.id.action_bookshelf_fragment_self,
                    BookshelfFragmentArgs(
                        server.id.value,
                        bookshelf.last().base64Path(),
                        position = position
                    ).toBundle()
                )
                delay(500)
            }
            logcat("RESTORE_NAVIGATION", LogPriority.INFO) { "Done restore navigation." }
            viewModel.shouldKeepOnScreen = false
        }
    }

    private fun performBiometricAuthentication(restoreOnLaunch: Boolean) {
        when (BiometricManager.from(this).canAuthenticate(BiometricUtil.authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                // 生体認証が利用できないため
                return
            }
            BiometricManager.BIOMETRIC_SUCCESS -> Unit
        }
        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(this@MainActivity, errString, Toast.LENGTH_SHORT).show()
                    ActivityCompat.finishAffinity(this@MainActivity)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (restoreOnLaunch) {
                        restoreNavigation()
                    } else {
                        viewModel.shouldKeepOnScreen = false
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        this@MainActivity,
                        androidx.biometric.R.string.fingerprint_not_recognized,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    ActivityCompat.finishAffinity(this@MainActivity)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("生体認証")
            .setSubtitle("確認のため、生体認証を行ってください")
            .setConfirmationRequired(false)
            .setAllowedAuthenticators(BiometricUtil.authenticators)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}
