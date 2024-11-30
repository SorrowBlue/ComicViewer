package com.sorrowblue.comicviewer.framework.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * A class which provides easy-to-use utilities for updating the System UI
 * bar colors within Jetpack Compose.
 *
 * @sample com.google.accompanist.sample.systemuicontroller.SystemUiControllerSample
 */
@Stable
interface SystemUiController {

    /**
     * Control for the behavior of the system bars. This value should
     * be one of the [WindowInsetsControllerCompat] behavior constants:
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH]
     * (Deprecated), [WindowInsetsControllerCompat.BEHAVIOR_DEFAULT] and
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE].
     */
    var systemBarsBehavior: Int

    /**
     * Property which holds the status bar visibility. If set to true, show the
     * status bar, otherwise hide the status bar.
     */
    var isStatusBarVisible: Boolean

    /**
     * Property which holds the navigation bar visibility. If set to true, show
     * the navigation bar, otherwise hide the navigation bar.
     */
    var isNavigationBarVisible: Boolean

    /**
     * Property which holds the status & navigation bar visibility. If set to
     * true, show both bars, otherwise hide both bars.
     */
    var isSystemBarsVisible: Boolean
        get() = isNavigationBarVisible && isStatusBarVisible
        set(value) {
            isStatusBarVisible = value
            isNavigationBarVisible = value
        }
}

/**
 * Remembers a [SystemUiController] for the given [window].
 *
 * If no [window] is provided, an attempt to find the correct [Window] is
 * made.
 *
 * First, if the [LocalView]'s parent is a [DialogWindowProvider], then
 * that dialog's [Window] will be used.
 *
 * Second, we attempt to find [Window] for the [Activity] containing the
 * [LocalView].
 *
 * If none of these are found (such as may happen in a preview), then the
 * functionality of the returned [SystemUiController] will be degraded, but
 * won't throw an exception.
 */
@Composable
fun rememberSystemUiController(
    window: Window? = findWindow(),
): SystemUiController {
    val view = LocalView.current
    return remember(view, window) { AndroidSystemUiController(window, view) }
}

@Composable
private fun findWindow(): Window? =
    (LocalView.current.parent as? DialogWindowProvider)?.window
        ?: LocalView.current.context.findWindow()

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

/**
 * A helper class for setting the navigation and status bar colors for a
 * [View], gracefully degrading behavior based upon API level.
 *
 * Typically you would use [rememberSystemUiController] to remember an
 * instance of this.
 */
internal class AndroidSystemUiController(
    window: Window?,
    private val view: View,
) : SystemUiController {
    private val windowInsetsController = window?.let {
        WindowCompat.getInsetsController(it, view)
    }

    override var systemBarsBehavior: Int
        get() = windowInsetsController?.systemBarsBehavior ?: 0
        set(value) {
            windowInsetsController?.systemBarsBehavior = value
        }

    override var isStatusBarVisible: Boolean
        get() {
            return ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.statusBars()) == true
        }
        set(value) {
            if (value) {
                windowInsetsController?.show(WindowInsetsCompat.Type.statusBars())
            } else {
                windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
            }
        }

    override var isNavigationBarVisible: Boolean
        get() {
            return ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.navigationBars()) == true
        }
        set(value) {
            if (value) {
                windowInsetsController?.show(WindowInsetsCompat.Type.navigationBars())
            } else {
                windowInsetsController?.hide(WindowInsetsCompat.Type.navigationBars())
            }
        }
}
